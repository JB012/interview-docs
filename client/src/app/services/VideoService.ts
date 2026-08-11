import { HttpClient } from "@angular/common/http";
import { inject, Injectable, isDevMode } from "@angular/core";
import { Observable } from "rxjs";
import { VideoType, PagedVideoType } from "../types/VideoType";
import { environment as devEnvironment} from "../environments/environment.development";
import { environment as prodEnvironment } from "../environments/environment";

@Injectable({providedIn: 'root'})
export class VideoService {
  private http = inject(HttpClient);
  apiHost = isDevMode() ? devEnvironment.url : prodEnvironment.url;

  getAllVideos(questionId : number, pageIndex = 0, pageSize = 10, sort = {field: "viewedAt", direction: 'desc'}): Observable<PagedVideoType> {
    return this.http.get<PagedVideoType>(`${this.apiHost}/videos?questionId=${questionId}&page=${pageIndex}&size=${pageSize}&field=${sort.field}&direction=${sort.direction}`, {
      withCredentials: true
    });
  }

  getVideo(id: number): Observable<VideoType> {
    return this.http.get<VideoType>(`${this.apiHost}/videos/${id}`, {
      withCredentials: true
    });
  }

  uploadVideoToS3(videoBuffer : File) : Observable<boolean> {
    const formData = new FormData();

    formData.append("videoBuffer", videoBuffer);

    return this.http.post<boolean>(`${this.apiHost}/videos/upload`, formData, {
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