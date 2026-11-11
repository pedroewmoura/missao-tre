import { request } from "./api";
export interface Polo {
    numero: number
    municipioSede: string;
}
export const poloService = {
    listarTodos(): Promise<Polo[]>{
        return request<Polo[]>('/polo')
    },
}