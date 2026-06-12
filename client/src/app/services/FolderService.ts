import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { FolderType, PagedFolderType } from "../types/FolderType";
import { PagedQuestionType, QuestionType } from "../types/QuestionType";

@Injectable({providedIn: 'root'})
export class FolderService {
  private http = inject(HttpClient);

  getFolders(pageIndex = 0, pageSize = 10, sort = {field: "viewedAt", direction: 'desc'}): Observable<PagedFolderType> {
    return this.http.get<PagedFolderType>(`http://localhost:8080/folders?page=${pageIndex}&size=${pageSize}&sort=${sort.field},${sort.direction}`, {
      withCredentials: true
    });
  }

  getAllFolders() : Observable<FolderType[]> {
    return this.http.get<FolderType[]>(`http://localhost:8080/folders/all`, {
      withCredentials: true
    });
  }
   
  getQuestionsInFolder(id : number, pageIndex = 0, pageSize = 10, sort = {field: "viewedAt", direction: 'desc'}): Observable<PagedQuestionType> {
    return this.http.get<PagedQuestionType>(`http://localhost:8080/folders/${id}/questions?page=${pageIndex}&size=${pageSize}&sort=${sort.field},${sort.direction}`, {
      withCredentials: true
    });
  }

  getAllQuestionsInFolder(id: number) : Observable<QuestionType[]> {
    return this.http.get<QuestionType[]>(`http://localhost:8080/folders/${id}/questions/all`, {
      withCredentials: true
    });
  }

  postQuestionInFolder(folderId: number, questionId: number) {
    return this.http.post(`http://localhost:8080/folders/${folderId}/questions/add`, questionId, {
      withCredentials: true
    });
  }

  deleteQuestionInFolder(folderId: number, questionId: number) {
    return this.http.delete(`http://localhost:8080/folders/${folderId}/questions/${questionId}/delete`, {
      withCredentials: true
    })
  }

  getFolder(id: number): Observable<FolderType> {
    return this.http.get<FolderType>(`http://localhost:8080/folders/${id}`, {
      withCredentials: true
    });
  }

  postFolder(folder: FolderType): Observable<FolderType> {
    return this.http.post<FolderType>(`http://localhost:8080/folders`, folder, {
      withCredentials: true
    });
  }

  putFolder(folder: FolderType, id: number) {
    return this.http.put(`http://localhost:8080/folders/${id}`, folder, {
      withCredentials: true
    });
  }
  
  deleteFolder(id: number) {
    return this.http.delete(`http://localhost:8080/folders/${id}`, {
      withCredentials: true
    });
  }
}