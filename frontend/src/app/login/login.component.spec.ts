import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { LoginComponent } from './login.component';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;
  let router: Router;

  beforeEach(async () => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['login']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['login']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit login when form is valid', () => {
    apiService.login.and.returnValue(of({ user: { id: 'u1', role: 'client' }, token: 't1' }));

    component.loginForm.setValue({ email: 'a@b.com', password: 'password1' });
    component.onSubmit();

    expect(apiService.login).toHaveBeenCalled();
    expect(authService.login).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
    expect(component.loginForm.value.email).toBeNull();
  });

  it('should not submit when form is invalid', () => {
    component.loginForm.setValue({ email: 'invalid', password: 'short' });
    component.onSubmit();

    expect(apiService.login).not.toHaveBeenCalled();
    expect(authService.login).not.toHaveBeenCalled();
  });
});
