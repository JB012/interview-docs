import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { FolderType, PagedFolderType } from "../types/FolderType";

@Injectable({providedIn: 'root'})
export class FolderService {
  private http = inject(HttpClient);

  getFolders(pageIndex = 0, pageSize = 10, sort = {field: "viewedAt", direction: 'desc'}): Observable<PagedFolderType> {
    return this.http.get<PagedFolderType>(`http://localhost:8080/folders?page=${pageIndex}&size=${pageSize}&sort=${sort.field},${sort.direction}`, {
      withCredentials: true
    });
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

  putFolder(folder: FolderType, id: number): Observable<FolderType> {
    return this.http.put<FolderType>(`http://localhost:8080/folders/${id}`, folder, {
      withCredentials: true
    });
  }
  
  deleteFolder(id: string): Observable<FolderType> {
    return this.http.delete<FolderType>(`http://localhost:8080/folders/${id}`, {
      withCredentials: true
    });
  }
}