import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { QuestionType, PagedQuestionType } from "../types/QuestionType";

@Injectable({providedIn: 'root'})
export class QuestionService {
  private http = inject(HttpClient);

  getQuestions(pageIndex = 0, pageSize = 10, sort = {field: "viewedAt", direction: 'desc'}): Observable<PagedQuestionType> {
    return this.http.get<PagedQuestionType>(`http://localhost:8080/questions?page=${pageIndex}&size=${pageSize}&sort=${sort.field},${sort.direction}`, {
      withCredentials: true
    });
  }

  getQuestion(id: number): Observable<QuestionType> {
    return this.http.get<QuestionType>(`http://localhost:8080/questions/${id}`, {
      withCredentials: true
    });
  }

  postQuestion(question: QuestionType): Observable<QuestionType> {
    return this.http.post<QuestionType>(`http://localhost:8080/questions`, question, {
      withCredentials: true
    });
  }

  putQuestion(question: QuestionType, id: number): Observable<QuestionType> {
    return this.http.put<QuestionType>(`http://localhost:8080/questions/${id}`, question, {
      withCredentials: true
    });
  }
  
  deleteQuestion(id: string): Observable<QuestionType> {
    return this.http.delete<QuestionType>(`http://localhost:8080/questions/${id}`, {
      withCredentials: true
    });
  }
}