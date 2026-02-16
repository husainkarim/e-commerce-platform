import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { SignupComponent, avatarValidator, passwordMatchValidator, roleValidator } from './signup.component';
import { ApiService } from '../api.service';

const makeControl = (value: any) => ({ value } as any);

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;
  let apiService: jasmine.SpyObj<ApiService>;
  let router: Router;

  beforeEach(async () => {
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['signup']);

    await TestBed.configureTestingModule({
      imports: [SignupComponent, RouterTestingModule],
      providers: [{ provide: ApiService, useValue: apiService }]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    spyOn(window, 'alert');

    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate password match', () => {
    const group: any = {
      get: (name: string) => (name === 'password' ? makeControl('abc') : makeControl('xyz'))
    };
    expect(passwordMatchValidator(group)).toEqual({ passwordMismatch: true });
  });

  it('should validate role and avatar', () => {
    expect(roleValidator(makeControl('client'))).toBeNull();
    expect(roleValidator(makeControl('invalid'))).toEqual({ invalidRole: true });

    expect(avatarValidator(makeControl('1.png'))).toBeNull();
    expect(avatarValidator(makeControl('invalid.png'))).toEqual({ invalidAvatar: true });
  });

  it('should submit signup when form is valid', () => {
    apiService.signup.and.returnValue(of({ success: true }));

    component.signupForm.setValue({
      name: 'User',
      email: 'u@e.com',
      password: 'password1',
      confirmPassword: 'password1',
      userType: 'client',
      avatar: '1.png'
    });

    component.onSubmit();

    expect(apiService.signup).toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
    expect(component.signupForm.value.name).toBeNull();
  });

  it('should not submit when form is invalid', () => {
    component.signupForm.setValue({
      name: 'Us',
      email: 'invalid',
      password: 'short',
      confirmPassword: 'nope',
      userType: 'invalid',
      avatar: 'invalid.png'
    });

    component.onSubmit();
    expect(apiService.signup).not.toHaveBeenCalled();
  });
});
