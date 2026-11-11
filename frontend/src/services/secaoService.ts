import { request } from './api'

export interface Secao {
    id: number
    numero: number
    zonaNumero: number
    municipioCodTse: number
    municipioNome?: string
    poloNumero?: number
}
export const secaoService = {
    listarTodos(): Promise<Secao[]>{
        return request<Secao[]> ('/secao')
    },
}