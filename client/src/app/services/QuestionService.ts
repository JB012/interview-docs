import { HttpClient } from "@angular/common/http";
import { inject, Injectable, isDevMode } from "@angular/core";
import { Observable } from "rxjs";
import { QuestionType, PagedQuestionType } from "../types/QuestionType";
import { FolderType } from "../types/FolderType";
import { environment as devEnvironment } from "../environments/environment.development";
import { environment as prodEnvironment } from "../environments/environment";

@Injectable({providedIn: 'root'})
export class QuestionService {
  private http = inject(HttpClient);
  apiHost = isDevMode() ? devEnvironment.url : prodEnvironment.url;

  getAllQuestions() : Observable<QuestionType[]> {
    return this.http.get<QuestionType[]>(`${this.apiHost}/questions/all`, {
      withCredentials: true
    });
  }

  getQuestions(pageIndex = 0, pageSize = 10, sort = {field: "viewedAt", direction: 'desc'}): Observable<PagedQuestionType> {
    return this.http.get<PagedQuestionType>(`${this.apiHost}/questions?page=${pageIndex}&size=${pageSize}&sort=${sort.field},${sort.direction}`, {
      withCredentials: true
    });
  }

  getQuestion(id: number): Observable<QuestionType> {
    return this.http.get<QuestionType>(`${this.apiHost}/questions/${id}`, {
      withCredentials: true
    });
  }

  getFoldersThatHasQuestion(id : number) : Observable<FolderType[]> {
    return this.http.get<FolderType[]>(`${this.apiHost}/questions/${id}/folders`, {
      withCredentials: true
    });
  }

  postQuestion(question: QuestionType): Observable<QuestionType> {
    return this.http.post<QuestionType>(`${this.apiHost}/questions`, question, {
      withCredentials: true
    });
  }

  putQuestion(question: QuestionType, id: number) {
    return this.http.put(`${this.apiHost}/questions/${id}`, question, {
      withCredentials: true
    });
  }
  
  deleteQuestion(id: number) {
    return this.http.delete(`${this.apiHost}/questions/${id}`, {
      withCredentials: true
    });
  }
}