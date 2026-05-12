import { Routes } from '@angular/router';
import { Home } from './routes/Home';
import { QuestionPage } from './routes/question-page';
import { NotFound } from './routes/NotFound';
import { LandingPage } from './routes/LandingPage';

export const routes: Routes = [
    {
        path: '', 
        component: LandingPage
    },
    {
        path: 'home',
        component: Home
    },
    {
        path: 'questions/:id', 
        component: QuestionPage
    },
    {
        path: '**', 
        component: NotFound
    }
];
