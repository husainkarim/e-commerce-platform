import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BehaviorSubject } from 'rxjs';
import { AppComponent } from './app.component';
import { AuthServiceService } from './auth-service.service';

describe('AppComponent', () => {
  let isLoggedInSubject: BehaviorSubject<boolean>;
  let userRoleSubject: BehaviorSubject<string>;
  let authService: jasmine.SpyObj<AuthServiceService> & {
    isLoggedIn$: BehaviorSubject<boolean>;
    userRole$: BehaviorSubject<string>;
  };

  beforeEach(async () => {
    isLoggedInSubject = new BehaviorSubject<boolean>(false);
    userRoleSubject = new BehaviorSubject<string>('guest');
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['logout']) as any;
    authService.isLoggedIn$ = isLoggedInSubject;
    authService.userRole$ = userRoleSubject;

    await TestBed.configureTestingModule({
      imports: [AppComponent, RouterTestingModule],
      providers: [{ provide: AuthServiceService, useValue: authService }]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should have the title', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('E-Commerce Platform');
  });

  it('should update role-based signals from auth service', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    userRoleSubject.next('seller');
    expect(app.isSellerOrAdminSignal()).toBeTrue();
    expect(app.isClientSignal()).toBeFalse();

    userRoleSubject.next('client');
    expect(app.isSellerOrAdminSignal()).toBeFalse();
    expect(app.isClientSignal()).toBeTrue();
  });

});
