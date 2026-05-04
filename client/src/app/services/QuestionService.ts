import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { QuestionType } from "../types/QuestionType";
import { PagedQuestionType } from "../types/PagedQuestionType";

@Injectable({providedIn: 'root'})
export class QuestionService {
  private http = inject(HttpClient);

  getQuestions(pageIndex = 0, pageSize = 7): Observable<PagedQuestionType> {
    return this.http.get<PagedQuestionType>(`http://localhost:8080/questions?page=${pageIndex}&size=${pageSize}`);
  }

  getQuestion(id: number): Observable<QuestionType> {
    return this.http.get<QuestionType>(`http://localhost:8080/questions/${id}`);
  }

  postQuestion(question: QuestionType): Observable<QuestionType> {
    return this.http.post<QuestionType>(`http://localhost:8080/questions`, question);
  }

  putQuestion(question: QuestionType, id: number): Observable<QuestionType> {
    return this.http.put<QuestionType>(`http://localhost:8080/questions/${id}`, question);
  }

  deleteQuestion(id: string): Observable<QuestionType> {
    return this.http.delete<QuestionType>(`http://localhost:8080/questions/${id}`);
  }

}