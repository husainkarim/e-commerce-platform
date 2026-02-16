import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { AuthServiceService } from './auth-service.service';

describe('AuthServiceService', () => {
  let service: AuthServiceService;

  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should initialize from localStorage when data exists', async () => {
    const savedUser = { id: 'u1', role: 'seller' };
    localStorage.setItem('user', JSON.stringify(savedUser));
    localStorage.setItem('authToken', 'token-1');

    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthServiceService);

    expect(service.getUser()).toEqual(savedUser);
    expect(service.getToken()).toBe('token-1');
    expect(service.isLoggedIn()).toBeTrue();
    expect(service.isSeller()).toBeTrue();

    const isLoggedIn = await firstValueFrom(service.isLoggedIn$);
    const role = await firstValueFrom(service.userRole$);
    expect(isLoggedIn).toBeTrue();
    expect(role).toBe('seller');
  });

  it('should initialize as guest when no saved state', async () => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthServiceService);

    expect(service.getUser()).toBeNull();
    expect(service.getToken()).toBeNull();
    expect(service.isLoggedIn()).toBeFalse();

    const isLoggedIn = await firstValueFrom(service.isLoggedIn$);
    const role = await firstValueFrom(service.userRole$);
    expect(isLoggedIn).toBeFalse();
    expect(role).toBe('guest');
  });

  it('should update state on login', async () => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthServiceService);

    const userData = { user: { id: 'u2', role: 'client' }, token: 'token-2' };
    service.login(userData);

    expect(service.getUser()).toEqual(userData.user);
    expect(service.getToken()).toBe('token-2');
    expect(service.isLoggedIn()).toBeTrue();
    expect(service.isClient()).toBeTrue();

    const isLoggedIn = await firstValueFrom(service.isLoggedIn$);
    const role = await firstValueFrom(service.userRole$);
    expect(isLoggedIn).toBeTrue();
    expect(role).toBe('client');
    expect(localStorage.getItem('authToken')).toBe('token-2');
    expect(localStorage.getItem('user')).toBe(JSON.stringify(userData.user));
  });

  it('should clear state on logout', async () => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthServiceService);

    service.login({ user: { id: 'u3', role: 'admin' }, token: 'token-3' });
    localStorage.setItem('cartItems', '[1]');
    service.logout();

    expect(service.getUser()).toBeNull();
    expect(service.getToken()).toBeNull();
    expect(service.isLoggedIn()).toBeFalse();
    expect(service.isAdmin()).toBeFalse();
    expect(localStorage.getItem('authToken')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
    expect(localStorage.getItem('cartItems')).toBeNull();

    const isLoggedIn = await firstValueFrom(service.isLoggedIn$);
    const role = await firstValueFrom(service.userRole$);
    expect(isLoggedIn).toBeFalse();
    expect(role).toBe('guest');
  });

  it('should update user and role helpers', () => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthServiceService);

    service.setUser({ id: 'u4', role: 'seller' });
    expect(service.isSeller()).toBeTrue();
    expect(service.isAdmin()).toBeFalse();
    expect(service.isClient()).toBeFalse();
  });
});
