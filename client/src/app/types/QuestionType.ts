export interface QuestionType {
    question_id?: number,
    user_id?: string,
    created_at?: string,
    edited_at?: string,
    viewed_at?: string
    question?: string,
    answer?: string,
    checked?: boolean
}

export interface PagedQuestionType {
    this: any
    content: QuestionType[]
    pageNumber: number,
    pageSize: number,
    totalSize: number
}