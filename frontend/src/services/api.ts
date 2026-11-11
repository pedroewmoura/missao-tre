const Base_URL = '/api'

export async function  request<T>(endpoint: string, options?: RequestInit): Promise<T> {
    const response = await fetch(`${Base_URL}${endpoint}`, {
        headers: {
            'Content-Type': 'application/json',
            ...options?.headers,
        },
        ...options,
    })

    if (!response.ok) {
        const errorMsg = await response.text()
        throw new Error(`Erro de requisição: ${response.status} - ${errorMsg || response.statusText}`)
    }

    return response.json() as Promise<T>
}
