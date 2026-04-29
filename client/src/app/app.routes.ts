import { Routes } from '@angular/router';
import { Home } from './routes/Home';
import { QuestionPage } from './routes/question-page';
import { NotFound } from './routes/NotFound';
import { LandingPage } from './routes/LandingPage';
import { userGuard } from './user-guard';

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
        path: 'questions/:id', 
        component: QuestionPage,
        canActivate: [userGuard]
    },
    {
        path: '**', 
        component: NotFound
    }
];
