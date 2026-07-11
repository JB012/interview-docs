export interface FolderType {
    folder_id?: number,
    user_id?: string,
    created_at?: string,
    edited_at?: string,
    viewed_at?: string,
    title?: string
    checked?: boolean
}

export interface PagedFolderType {
    content: FolderType[]
    pageNumber: number,
    pageSize: number,
    totalSize: number  
}