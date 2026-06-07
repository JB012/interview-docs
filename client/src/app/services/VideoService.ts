import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { VideoType, PagedVideoType } from "../types/VideoType";

@Injectable({providedIn: 'root'})
export class VideoService {
  private http = inject(HttpClient);

  getAllVideos(pageIndex = 0, pageSize = 10, sort = {field: "viewedAt", direction: 'desc'}): Observable<PagedVideoType> {
    return this.http.get<PagedVideoType>(`http://localhost:8080/videos?page=${pageIndex}&size=${pageSize}&sort=${sort.field},${sort.direction}`, {
      withCredentials: true
    });
  }

  getVideo(id: number): Observable<VideoType> {
    return this.http.get<VideoType>(`http://localhost:8080/videos/${id}`, {
      withCredentials: true
    });
  }

  postVideo(video: VideoType) : Observable<VideoType> {
    return this.http.post<VideoType>(`http://localhost:8080/videos`, video, {
      withCredentials: true
    });
  }

  putVideo(video: VideoType, id: number) {
    return this.http.put(`http://localhost:8080/videos/${id}`, {
        video: video
    }, {
      withCredentials: true
    });
  }

  deleteVideo(id: number) {
    return this.http.delete(`http://localhost:8080/videos/${id}`, {
      withCredentials: true
    });
  }
}