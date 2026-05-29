import { Routes } from '@angular/router';
import { Home } from './routes/Home';
import { QuestionPage } from './routes/question-page';
import { NotFound } from './routes/NotFound';
import { LandingPage } from './routes/LandingPage';
import { userGuard } from './user-guard';
import { RecordedVideo } from './routes/RecordedVideo';
import { VideoPage } from './routes/VideoPage';
import { VideoList } from './routes/VideoList';
import { FolderList } from './routes/FolderList';
import { FolderPage } from './routes/FolderPage';

export const routes: Routes = [
    {
        path: '', 
        component: LandingPage
    },
    {
        path: 'home',
        component: Home,
        canActivate: [userGuard]
    },
    {
        path: 'folders',
        component: FolderList,
        canActivate: [userGuard],
        children: [
            {
                path: ':/id',
                component: FolderPage,
                canActivate: [userGuard]
            }
        ]
    },
    {
        path: 'questions/:questionId', 
        children: [
            {
                path: 'videos',
                children: [
                    {
                        path: '',
                        component: VideoList,
                        canActivate: [userGuard]
                    },
                    {
                        path: 'new',
                        component: RecordedVideo,
                        canActivate: [userGuard]
                    },
                    {
                        path: ':videoId',
                        component: RecordedVideo,
                        canActivate: [userGuard] 
                    }
                ],
                component: VideoPage,
                canActivate: [userGuard]
            }
        ],
        component: QuestionPage,
        canActivate: [userGuard]
    },
    {
        path: '**', 
        component: NotFound,
        canActivate: [userGuard]
    }
];
