import { request } from './api'

export interface Zona {
    numero: number
    municipioSede: string
    municipiosCodTse?: number[]
}

export const zonaService = {
    listarTodos(): Promise<Zona[]> {
        return request<Zona[]>('/zona')
    }
}
