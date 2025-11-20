import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthServiceService } from '../auth-service.service'; // adjust path

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthServiceService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      switch (error.status) {
        case 0:
          console.error('Network error:', error.message);
          break;
        case 400:
          console.warn('Bad request:', error.error);
          // router.navigate(['/bad-request']);
          break;
        case 401:
          console.warn('Unauthorized — logging out...');
          // router.navigate(['/unauthorized']);
          // authService.logout();
          break;
        case 403:
          console.warn('Forbidden');
          // router.navigate(['/forbidden']);
          break;
        case 404:
          console.warn('Not found');
          router.navigate(['/not-found']);
          break;
        case 408:
          console.warn('Request timeout');
          break;
        case 409:
          console.warn('Conflict:', error.error);
          router.navigate(['/conflict']);
          break;
        case 429:
          console.warn('Too many requests — please slow down');
          break;
        case 500:
          console.error('Server error');
          router.navigate(['/server-error']);
          break;
        default:
          console.error('Unhandled error:', error);
      }

      return throwError(() => error);
    })
  );
};
