import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { VideoType } from "../types/VideoType";

@Injectable({providedIn: 'root'})
export class VideoService {
  private http = inject(HttpClient);

  getAllVideos(): Observable<VideoType[]> {
    return this.http.get<VideoType[]>(`http://localhost:8080/videos`);
  }

  getVideo(id: number): Observable<VideoType> {
    return this.http.get<VideoType>(`http://localhost:8080/videos/${id}`);
  }

  postVideo(question: VideoType): Observable<VideoType> {
    return this.http.post<VideoType>(`http://localhost:8080/videos`, {
        question: question
    });
  }

  putVideo(video: VideoType, id: number): Observable<VideoType> {
    return this.http.put<VideoType>(`http://localhost:8080/videos/${id}`, {
        video: video
    });
  }

  deleteVideo(id: string): Observable<VideoType> {
    return this.http.delete<VideoType>(`http://localhost:8080/video/${id}`);
  }

}