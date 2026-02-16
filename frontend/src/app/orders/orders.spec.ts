import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Orders } from './orders';
import { AuthServiceService } from '../auth-service.service';

describe('Orders', () => {
  let component: Orders;
  let fixture: ComponentFixture<Orders>;
  let authService: jasmine.SpyObj<AuthServiceService>;
  let router: Router;

  const setup = async (loggedIn: boolean, role?: string) => {
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn', 'getUser', 'getToken']);
    authService.isLoggedIn.and.returnValue(loggedIn);
    authService.getUser.and.returnValue(role ? { role } : null);
    authService.getToken.and.returnValue('mock-token');

    await TestBed.configureTestingModule({
      imports: [Orders, RouterTestingModule, HttpClientTestingModule],
      providers: [{ provide: AuthServiceService, useValue: authService }]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(Orders);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(() => {
    TestBed.resetTestingModule();
  });

  it('should create for client role', async () => {
    await setup(true, 'client');
    expect(component.isClient).toBeTrue();
    expect(component.isSeller).toBeFalse();
  });

  it('should create for seller role', async () => {
    await setup(true, 'seller');
    expect(component.isSeller).toBeTrue();
    expect(component.isClient).toBeFalse();
  });

  it('should redirect to login when not logged in', async () => {
    await setup(false);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should redirect to unauthorized when role is not client or seller', async () => {
    await setup(true, 'admin');
    expect(router.navigate).toHaveBeenCalledWith(['/unauthorized']);
  });
});
