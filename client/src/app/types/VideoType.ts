export interface VideoType {
    video_id?: number,
    created_at?: string,
    edited_at?: string,
    viewed_at?: string,
    user_id?: string
    source?: string,
    title?: string,
}

export interface PagedVideoType {
    content: VideoType[]
    pageNumber: number,
    pageSize: number,
    totalSize: number 
}