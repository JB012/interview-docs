import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { QuestionType } from "../types/QuestionType";

@Injectable({providedIn: 'root'})
export class QuestionService {
  private http = inject(HttpClient);

  getAllQuestions(): Observable<QuestionType[]> {
    return this.http.get<QuestionType[]>(`http://localhost:8080/questions`);
  }

  getQuestion(id: number): Observable<QuestionType> {
    return this.http.get<QuestionType>(`http://localhost:8080/questions/${id}`);
  }

  postQuestion(question: QuestionType): Observable<QuestionType> {
    console.log(question);
    return this.http.post<QuestionType>(`http://localhost:8080/questions`, question);
  }

  putQuestion(question: QuestionType, id: number): Observable<QuestionType> {
    return this.http.put<QuestionType>(`http://localhost:8080/questions/${id}`, question);
  }

  deleteQuestion(id: string): Observable<QuestionType> {
    return this.http.delete<QuestionType>(`http://localhost:8080/questions/${id}`);
  }

}