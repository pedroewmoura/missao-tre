import { request } from './api'
import type { Polo } from './poloService'

export interface Municipio {
    codTse: number
    nome: string
    poloNumero?: number
    polo?: Polo
}

export const municipioService = {
    listarTodos(): Promise<Municipio[]> {
        return request<Municipio[]>('/municipio')
    },
}
