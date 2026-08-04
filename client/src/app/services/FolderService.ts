import { HttpClient } from "@angular/common/http";
import { inject, Injectable, isDevMode } from "@angular/core";
import { Observable } from "rxjs";
import { FolderType, PagedFolderType } from "../types/FolderType";
import { PagedQuestionType, QuestionType } from "../types/QuestionType";
import { environment as devEnvironment} from "../environments/environment.development";
import { environment as prodEnvironment } from "../environments/environment";

@Injectable({providedIn: 'root'})
export class FolderService {
  private http = inject(HttpClient);
  apiHost = isDevMode() ? devEnvironment.url : prodEnvironment.url;

  getFolders(pageIndex = 0, pageSize = 10, sort = {field: "viewedAt", direction: 'desc'}): Observable<PagedFolderType> {
    return this.http.get<PagedFolderType>(`${this.apiHost}/folders?page=${pageIndex}&size=${pageSize}&field=${sort.field}&direction=${sort.direction}`, {
      withCredentials: true
    });
  }

  getAllFolders() : Observable<FolderType[]> {
    return this.http.get<FolderType[]>(`${this.apiHost}/folders/all`, {
      withCredentials: true
    });
  }
  
  getQuestionsInFolder(id : number, pageIndex = 0, pageSize = 10, sort = {field: "viewedAt", direction: 'desc'}): Observable<PagedQuestionType> {
    return this.http.get<PagedQuestionType>(`${this.apiHost}/folders/${id}/questions?page=${pageIndex}&size=${pageSize}&field=${sort.field}&direction=${sort.direction}`, {
      withCredentials: true
    });
  }

  getAllQuestionsInFolder(id: number) : Observable<QuestionType[]> {
    return this.http.get<QuestionType[]>(`${this.apiHost}/folders/${id}/questions/all`, {
      withCredentials: true
    });
  }

  postQuestionInFolder(folderId: number, questionId: number) {
    return this.http.post(`${this.apiHost}/folders/${folderId}/questions/add`, questionId, {
      withCredentials: true
    });
  }

  deleteQuestionInFolder(folderId: number, questionId: number) {
    return this.http.delete(`${this.apiHost}/folders/${folderId}/questions/${questionId}/delete`, {
      withCredentials: true
    })
  }

  getFolder(id: number): Observable<FolderType> {
    return this.http.get<FolderType>(`${this.apiHost}/folders/${id}`, {
      withCredentials: true
    });
  }

  postFolder(folder: FolderType): Observable<FolderType> {
    return this.http.post<FolderType>(`${this.apiHost}/folders`, folder, {
      withCredentials: true
    });
  }

  putFolder(folder: FolderType, id: number) {
    return this.http.put(`${this.apiHost}/folders/${id}`, folder, {
      withCredentials: true
    });
  }
  
  deleteFolder(id: number) {
    return this.http.delete(`${this.apiHost}/folders/${id}`, {
      withCredentials: true
    });
  }
}