import { signal } from '@angular/core';

class RouterMock {
  private currentUrl = signal('/');
  public currentUrl$ = this.currentUrl.asReadonly();

  navigate(path: string[]) {
    const newUrl = path.join('/');
    this.currentUrl.set(newUrl);
    console.log(`Navigation Mock: Navigating to ${newUrl}`);
  }
}
export const routerMock = new RouterMock();
