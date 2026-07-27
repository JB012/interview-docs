import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { VideoType, PagedVideoType } from "../types/VideoType";
import { environment } from "../environments/environment.development";

@Injectable({providedIn: 'root'})
export class VideoService {
  private http = inject(HttpClient);
  apiHost = environment.url;

  getAllVideos(questionId : number, pageIndex = 0, pageSize = 10, sort = {field: "viewedAt", direction: 'desc'}): Observable<PagedVideoType> {
    return this.http.get<PagedVideoType>(`${this.apiHost}/videos?questionId=${questionId}&page=${pageIndex}&size=${pageSize}&sort=${sort.field},${sort.direction}`, {
      withCredentials: true
    });
  }

  getVideo(id: number): Observable<VideoType> {
    return this.http.get<VideoType>(`${this.apiHost}/videos/${id}`, {
      withCredentials: true
    });
  }

  postVideo(video: VideoType, questionId : number) : Observable<VideoType> {
    return this.http.post<VideoType>(`${this.apiHost}/videos?questionId=${questionId}`, video, {
      withCredentials: true
    });
  }

  putVideo(video: VideoType, id: number) {
    return this.http.put(`${this.apiHost}/videos/${id}`, video, {
      withCredentials: true
    });
  }

  deleteVideo(id: number) {
    return this.http.delete(`${this.apiHost}/videos/${id}`, {
      withCredentials: true
    });
  }
}