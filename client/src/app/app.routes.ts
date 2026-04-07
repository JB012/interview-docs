import { Routes } from '@angular/router';
import { Home } from './routes/Home';
import { QuestionPage } from './routes/question-page';
import { NotFound } from './routes/NotFound';

export const routes: Routes = [
    {path: '', component: Home},
    {path: 'questions/:id', component: QuestionPage},
    {path: '**', component: NotFound}
];
