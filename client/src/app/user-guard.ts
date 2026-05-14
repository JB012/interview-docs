import { CanActivateFn } from '@angular/router';
import { AuthService } from './services/AuthService';
import { inject } from '@angular/core';
import { map, take } from 'rxjs';

export const userGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  return auth.getCurrentUser().pipe(map(res => res?.authenticated ?? false), take(1));
};
