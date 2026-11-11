package missao.demo.service;

import lombok.extern.slf4j.Slf4j;
import missao.demo.model.Municipio;
import missao.demo.model.Polo;
import missao.demo.model.Secao;
import missao.demo.model.Zona;
import missao.demo.repository.MunicipioRepository;
import missao.demo.repository.PoloRepository;
import missao.demo.repository.SecaoRepository;
import missao.demo.repository.ZonaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class CsvImportService implements CommandLineRunner {

    private final MunicipioRepository municipioRepository;
    private final PoloRepository poloRepository;
    private final SecaoRepository secaoRepository;
    private final ZonaRepository zonaRepository;

    public CsvImportService(
            MunicipioRepository municipioRepository,
            PoloRepository poloRepository,
            SecaoRepository secaoRepository,
            ZonaRepository zonaRepository) {
        this.municipioRepository = municipioRepository;
        this.poloRepository = poloRepository;
        this.secaoRepository = secaoRepository;
        this.zonaRepository = zonaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("========== INICIANDO PROCESSO DE IMPORTAÇÃO CSV ==========");
        importarPolos();
        importarZonas();
        importarMunicipios();
        importarSecoes();
        vincularPolosEMunicipiosSeNecessario();
        log.info("========== IMPORTAÇÃO CSV CONCLUÍDA COM SUCESSO ==========");
    }

    // 1. Importa os Polos a partir de polos-sedes.csv
    private void importarPolos() {
        if (poloRepository.count() > 0) {
            log.info("Polos já cadastrados. Pulando importação.");
            return;
        }

        log.info("Importando Polos...");
        List<Polo> polos = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("csv/polos-sedes.csv").getInputStream(), StandardCharsets.UTF_8))) {

            String linha;
            boolean cabecalho = true;

            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) continue;
                if (cabecalho) { cabecalho = false; continue; }

                String[] colunas = linha.split(",", -1);
                if (colunas.length >= 2) {
                    Integer numeroPolo = Integer.parseInt(colunas[0].trim());
                    String codMunicipioSede = colunas[1].trim();

                    Polo polo = new Polo();
                    polo.setNumero(numeroPolo);
                    polo.setMunicipioSede(codMunicipioSede);
                    polos.add(polo);
                }
            }

            poloRepository.saveAll(polos);
            log.info("Total de Polos importados: {}", polos.size());

        } catch (Exception e) {
            log.error("Erro ao importar Polos: {}", e.getMessage(), e);
        }
    }

    // 2. Importa as Zonas a partir de zonas-sedes.csv
    private void importarZonas() {
        if (zonaRepository.count() > 0) {
            log.info("Zonas já cadastradas. Pulando importação.");
            return;
        }

        log.info("Importando Zonas...");
        List<Zona> zonas = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("csv/zonas-sedes.csv").getInputStream(), StandardCharsets.UTF_8))) {

            String linha;
            boolean cabecalho = true;

            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) continue;
                if (cabecalho) { cabecalho = false; continue; }

                String[] colunas = linha.split(",", -1);
                if (colunas.length >= 2) {
                    Integer numeroZona = Integer.parseInt(colunas[0].trim());
                    String codMunicipioSede = colunas[1].trim();

                    Zona zona = new Zona();
                    zona.setNumero(numeroZona);
                    zona.setMunicipioSede(codMunicipioSede);
                    zonas.add(zona);
                }
            }

            zonaRepository.saveAll(zonas);
            log.info("Total de Zonas importadas: {}", zonas.size());

        } catch (Exception e) {
            log.error("Erro ao importar Zonas: {}", e.getMessage(), e);
        }
    }

    // 3. Extrai e importa os Municípios distintos a partir de secoes.csv vinculando seus Polos
    private void importarMunicipios() {
        if (municipioRepository.count() > 0) {
            log.info("Municípios já cadastrados. Pulando importação inicial.");
            return;
        }

        log.info("Importando Municípios a partir de secoes.csv com vínculo aos Polos...");
        Map<Integer, Polo> polosMap = new HashMap<>();
        poloRepository.findAll().forEach(p -> polosMap.put(p.getNumero(), p));

        Map<Integer, Municipio> municipiosMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("csv/secoes.csv").getInputStream(), StandardCharsets.UTF_8))) {

            String linha;
            boolean cabecalho = true;

            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) continue;
                if (cabecalho) { cabecalho = false; continue; }

                String[] colunas = linha.split(",", -1);
                if (colunas.length >= 5) {
                    Integer codTse = Integer.parseInt(colunas[0].trim());
                    String nome = colunas[1].trim();
                    Integer numeroPolo = Integer.parseInt(colunas[4].trim());

                    // Evita duplicatas em memória antes de salvar
                    if (!municipiosMap.containsKey(codTse)) {
                        Municipio m = new Municipio();
                        m.setCodTse(codTse);
                        m.setNome(nome);
                        m.setPolo(polosMap.get(numeroPolo));
                        municipiosMap.put(codTse, m);
                    }
                }
            }

            municipioRepository.saveAll(municipiosMap.values());
            log.info("Total de Municípios únicos importados: {}", municipiosMap.size());

        } catch (Exception e) {
            log.error("Erro ao importar Municípios: {}", e.getMessage(), e);
        }
    }

    // 4. Importa as Seções vinculando com Municípios, Zonas e Polos
    private void importarSecoes() {
        if (secaoRepository.count() > 0) {
            log.info("Seções já cadastradas. Pulando importação.");
            return;
        }

        log.info("Carregando tabelas de apoio na memória para vincular as seções...");

        Map<Integer, Municipio> municipios = new HashMap<>();
        municipioRepository.findAll().forEach(m -> municipios.put(m.getCodTse(), m));

        Map<Integer, Zona> zonas = new HashMap<>();
        zonaRepository.findAll().forEach(z -> zonas.put(z.getNumero(), z));

        Map<Integer, Polo> polos = new HashMap<>();
        poloRepository.findAll().forEach(p -> polos.put(p.getNumero(), p));

        log.info("Iniciando leitura e gravação das Seções...");

        int novas = 0;
        int ignoradas = 0;
        List<Secao> lote = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("csv/secoes.csv").getInputStream(), StandardCharsets.UTF_8))) {

            String linha;
            boolean cabecalho = true;

            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) continue;
                if (cabecalho) { cabecalho = false; continue; }

                String[] colunas = linha.split(",", -1);
                if (colunas.length != 5) {
                    ignoradas++;
                    continue;
                }

                try {
                    Integer codigoMunicipio = Integer.parseInt(colunas[0].trim());
                    Integer numeroSecao     = Integer.parseInt(colunas[2].trim());
                    Integer numeroZona      = Integer.parseInt(colunas[3].trim());
                    Integer numeroPolo      = Integer.parseInt(colunas[4].trim());

                    Municipio municipio = municipios.get(codigoMunicipio);
                    Zona zona = zonas.get(numeroZona);
                    Polo polo = polos.get(numeroPolo);

                    if (municipio == null || zona == null || polo == null) {
                        ignoradas++;
                        continue;
                    }

                    // Vincula zona e município se ainda não vinculado
                    if (!zona.getMunicipios().contains(municipio)) {
                        zona.getMunicipios().add(municipio);
                    }

                    Secao secao = new Secao();
                    secao.setNumero(numeroSecao);
                    secao.setMunicipio(municipio);
                    secao.setZona(zona);
                    secao.setPolo(polo);

                    lote.add(secao);
                    novas++;

                    // Salva em blocos de 1.000 para economizar memória e ser rápido
                    if (lote.size() >= 1000) {
                        secaoRepository.saveAll(lote);
                        lote.clear();
                    }

                } catch (NumberFormatException e) {
                    ignoradas++;
                }
            }

            // Salva o restante do último lote
            if (!lote.isEmpty()) {
                secaoRepository.saveAll(lote);
                lote.clear();
            }

            // Atualiza as zonas com os municípios vinculados
            zonaRepository.saveAll(zonas.values());

            log.info("Importação de Seções finalizada! Novas: {} | Ignoradas: {}", novas, ignoradas);

        } catch (Exception e) {
            log.error("Erro na importação de Seções: {}", e.getMessage(), e);
        }
    }

    // 5. Garante que municípios sem polo recebam o polo correto a partir do CSV ou das seções
    private void vincularPolosEMunicipiosSeNecessario() {
        List<Municipio> semPolo = municipioRepository.findAll().stream()
                .filter(m -> m.getPolo() == null)
                .toList();

        if (semPolo.isEmpty()) {
            return;
        }

        log.info("Detectados {} municípios sem polo. Atualizando vínculos...", semPolo.size());
        Map<Integer, Polo> polosMap = new HashMap<>();
        poloRepository.findAll().forEach(p -> polosMap.put(p.getNumero(), p));

        Map<Integer, Integer> municipioPoloMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("csv/secoes.csv").getInputStream(), StandardCharsets.UTF_8))) {

            String linha;
            boolean cabecalho = true;

            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) continue;
                if (cabecalho) { cabecalho = false; continue; }

                String[] colunas = linha.split(",", -1);
                if (colunas.length >= 5) {
                    Integer codTse = Integer.parseInt(colunas[0].trim());
                    Integer numPolo = Integer.parseInt(colunas[4].trim());
                    municipioPoloMap.putIfAbsent(codTse, numPolo);
                }
            }

            for (Municipio m : semPolo) {
                Integer numPolo = municipioPoloMap.get(m.getCodTse());
                if (numPolo != null && polosMap.containsKey(numPolo)) {
                    m.setPolo(polosMap.get(numPolo));
                }
            }

            municipioRepository.saveAll(semPolo);
            log.info("Vínculos de polo_numero para municípios atualizados com sucesso!");

        } catch (Exception e) {
            log.error("Erro ao vincular polos aos municípios: {}", e.getMessage(), e);
        }
    }
}
