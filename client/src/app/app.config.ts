import { provideAnimations } from '@angular/platform-browser/animations';
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { CustomPaginator } from './providers/CustomPaginator';

export const appConfig: ApplicationConfig = {
  providers: [
    {provide: MatPaginatorIntl, useValue: CustomPaginator()},
    provideAnimations(),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
  ],
};
