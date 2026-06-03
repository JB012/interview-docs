export interface FolderType {
    folder_id?: number,
    user_id?: string,
    created_at?: string,
    edited_at?: string,
    viewed_at?: string,
    title?: string
}

export interface PagedFolderType {
    content: FolderType[]
    page: {
        size: number
        number: number
        totalElements: number
        totalPages: number
    }  
}