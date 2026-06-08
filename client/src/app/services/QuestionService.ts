import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { QuestionType, PagedQuestionType } from "../types/QuestionType";
import { FolderType } from "../types/FolderType";

@Injectable({providedIn: 'root'})
export class QuestionService {
  private http = inject(HttpClient);

  getAllQuestions() : Observable<QuestionType[]> {
    return this.http.get<QuestionType[]>('http://localhost:8080/questions/all', {
      withCredentials: true
    });
  }

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

  putQuestion(question: QuestionType, id: number) {
    return this.http.put(`http://localhost:8080/questions/${id}`, question, {
      withCredentials: true
    });
  }
  
  deleteQuestion(id: number) {
    return this.http.delete(`http://localhost:8080/questions/${id}`, {
      withCredentials: true
    });
  }

  getFolders(id: number) : Observable<FolderType[]> {
    return this.http.get<FolderType[]>(`http://localhost:8080/questions/${id}/folders`, {
      withCredentials: true
    });
  } 
}