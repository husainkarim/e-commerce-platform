import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { ProfileComponent } from './profile.component';
import { ApiService } from '../api.service';
import { AuthServiceService } from '../auth-service.service';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;
  let router: Router;

  const baseUser = { id: 'u1', name: 'User', email: 'u@e.com', role: 'seller', avatar: '1.png' };

  const setup = async (routeId?: string, loggedIn = true) => {
    const user = { ...baseUser };
    apiService = jasmine.createSpyObj<ApiService>('ApiService', [
      'profile',
      'deleteAccount',
      'getUserProducts',
      'getImagesByProductId',
      'deleteProduct',
      'getClientData'
    ]);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['getUser', 'isLoggedIn', 'logout']);

    authService.getUser.and.returnValue(user);
    authService.isLoggedIn.and.returnValue(loggedIn);
    apiService.profile.and.returnValue(of({ user }));
    apiService.deleteAccount.and.returnValue(of({}));
    apiService.getUserProducts.and.returnValue(of({ sellerDashboard: { products: [], totalRevenue: 0, totalUnitsSold: 0 } }));
    apiService.getImagesByProductId.and.returnValue(of({ images: [] }));
    apiService.deleteProduct.and.returnValue(of({}));
    apiService.getClientData.and.returnValue(of({
      totalSpent: 0,
      totalOrders: 0,
      totalItemsPurchased: 0,
      mostBoughtProducts: [],
      topCategories: [],
      recentOrders: []
    }));

    await TestBed.configureTestingModule({
      imports: [ProfileComponent, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap(routeId ? { id: routeId } : {}) } }
        }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(() => {
    TestBed.resetTestingModule();
  });

  it('should create and load profile by route id', async () => {
    await setup('u1');
    expect(component.user.id).toBe('u1');
    expect(component.isSeller).toBeTrue();
    expect(component.showDashboard).toBeTrue();
  });

  it('should redirect when not logged in', async () => {
    await setup('u1', false);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should update role flags from setUserRole', async () => {
    await setup();
    component.user.role = 'client';
    component.setUserRole();
    expect(component.isClient).toBeTrue();
    expect(component.isSeller).toBeFalse();
  });

  it('should navigate to edit profile', async () => {
    await setup();
    component.openEditProfileModal();
    expect(router.navigate).toHaveBeenCalledWith(['/edit-profile/u1']);
  });

  it('should delete account when confirmed', async () => {
    await setup();
    spyOn(window, 'confirm').and.returnValue(true);

    component.deleteAccount();

    expect(apiService.deleteAccount).toHaveBeenCalledWith('u1');
    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
