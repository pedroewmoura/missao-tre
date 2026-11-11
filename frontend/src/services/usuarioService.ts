import { request } from "./api";

export interface Usuario {
    cpf: string
    nome: string
    email: string
}

export const usuarioService = {
    listarTodos(): Promise<Usuario[]> {
        return request<Usuario[]>('/usuarios')
    },
    buscarPorId(id: number): Promise<Usuario> {
        return request<Usuario>(`usuarios/${id}`)
    },
    cadastrar(usuario: Usuario): Promise<Usuario> {
        return request<Usuario>('/usuarios', {
            method: 'POST',
            body: JSON.stringify(usuario)
        })
    },
    deletar(id: number): Promise<void> {
        return request<void>(`/usuarios/${id}`,{
            method: 'DELETE'
        })
    }
}