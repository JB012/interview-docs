export interface QuestionType {
    id?: number,
    folder_id?: number,
    user_id?: string,
    created_at?: string,
    edited_at?: string,
    viewed_at?: string
    question?: string,
    answer?: string,
    checked?: boolean
}

export interface PagedQuestionType {
    content: QuestionType[]
    page: {
        size: number
        number: number
        totalElements: number
        totalPages: number
    }  
}