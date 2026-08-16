import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Role } from '../models/auth.models';

/** Requires a signed-in user; otherwise sends to /login remembering where you were headed. */
export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) {
    return true;
  }
  return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};

/**
 * Restricts a route to the roles listed in route data.
 * Bracket access is required - tsconfig sets noPropertyAccessFromIndexSignature.
 */
export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const allowed = (route.data['roles'] as Role[] | undefined) ?? [];
  const user = auth.currentUser();

  if (user && allowed.includes(user.role)) {
    return true;
  }
  // Send people to their own home rather than a dead end.
  return router.createUrlTree([auth.homeRoute()]);
};

/** Keeps signed-in users away from /login and /register. */
export const guestGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) {
    return router.createUrlTree([auth.homeRoute()]);
  }
  return true;
};
