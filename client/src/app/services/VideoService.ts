import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { VideoType } from "../types/VideoType";

@Injectable({providedIn: 'root'})
export class VideoService {
  private http = inject(HttpClient);

  getAllVideos(): Observable<VideoType[]> {
    return this.http.get<VideoType[]>(`http://localhost:8080/videos`, {
      withCredentials: true
    });
  }

  getVideo(id: number): Observable<VideoType> {
    return this.http.get<VideoType>(`http://localhost:8080/videos/${id}`, {
      withCredentials: true
    });
  }

  postVideo(video: VideoType) {
    return this.http.post(`http://localhost:8080/videos`, video, {
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

  deleteVideo(id: string) {
    return this.http.delete(`http://localhost:8080/videos/${id}`, {
      withCredentials: true
    });
  }

}