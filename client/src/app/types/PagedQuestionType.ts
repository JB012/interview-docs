import { QuestionType } from "./QuestionType"

export interface PagedQuestionType {
  content: QuestionType[]
  page: {
    size: number
    number: number
    totalElements: number
    totalPages: number
  }  
}