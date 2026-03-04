import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, NavigationEnd } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Subject, of } from 'rxjs';
import { SellerDashboardComponent } from './seller-dashboard.component';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

describe('SellerDashboardComponent', () => {
  let component: SellerDashboardComponent;
  let fixture: ComponentFixture<SellerDashboardComponent>;
  let apiService: jasmine.SpyObj<ApiService>;
  let authService: jasmine.SpyObj<AuthServiceService>;
  let router: Router;
  let routerEvents: Subject<any>;

  beforeEach(async () => {
    routerEvents = new Subject<any>();
    apiService = jasmine.createSpyObj<ApiService>('ApiService', ['getUserProducts', 'getImagesByProductId', 'deleteProduct']);
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn', 'getUser']);

    authService.isLoggedIn.and.returnValue(true);
    authService.getUser.and.returnValue({ id: 'u1' });

    apiService.getUserProducts.and.returnValue(of({
      sellerDashboard: {
        products: [
          { id: 1, name: 'A', unitsSold: 5, revenue: 100 },
          { id: 2, name: 'B', unitsSold: 10, revenue: 50 }
        ],
        totalRevenue: 150,
        totalUnitsSold: 15
      }
    }));
    apiService.getImagesByProductId.and.returnValue(of({ images: [] }));
    apiService.deleteProduct.and.returnValue(of({}));

    await TestBed.configureTestingModule({
      imports: [SellerDashboardComponent, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiService },
        { provide: AuthServiceService, useValue: authService }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    routerEvents = new Subject<any>();
    spyOnProperty(router, 'events', 'get').and.returnValue(routerEvents.asObservable());
    fixture = TestBed.createComponent(SellerDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and load products', () => {
    expect(component).toBeTruthy();
    expect(component.products.length).toBe(2);
    expect(component.totalRevenue).toBe(150);
    expect(component.totalUnitsSold).toBe(15);
    expect(component.topSellingProductsByUnits[0].id).toBe(2);
  });

  it('should refresh on navigation end', () => {
    apiService.getUserProducts.calls.reset();
    routerEvents.next(new NavigationEnd(1, '/a', '/b'));
    expect(apiService.getUserProducts).toHaveBeenCalled();
  });

  it('should delete product and refresh', () => {
    spyOn(component, 'getUserProducts');
    component.deleteProduct('p1');
    expect(apiService.deleteProduct).toHaveBeenCalledWith('p1');
    expect(component.getUserProducts).toHaveBeenCalled();
  });

  it('should redirect when not logged in', async () => {
    await TestBed.resetTestingModule();
    const apiMock = jasmine.createSpyObj<ApiService>('ApiService', ['getUserProducts', 'getImagesByProductId', 'deleteProduct']);
    const authMock = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['isLoggedIn', 'getUser']);
    authMock.isLoggedIn.and.returnValue(false);
    authMock.getUser.and.returnValue({ id: 'u1' });
    apiMock.getUserProducts.and.returnValue(of({ sellerDashboard: { products: [], totalRevenue: 0, totalUnitsSold: 0 } }));
    apiMock.getImagesByProductId.and.returnValue(of({ images: [] }));

    await TestBed.configureTestingModule({
      imports: [SellerDashboardComponent, RouterTestingModule],
      providers: [
        { provide: ApiService, useValue: apiMock },
        { provide: AuthServiceService, useValue: authMock }
      ]
    }).compileComponents();

    const localRouter = TestBed.inject(Router);
    spyOn(localRouter, 'navigate');
    const localEvents = new Subject<any>();
    spyOnProperty(localRouter, 'events', 'get').and.returnValue(localEvents.asObservable());

    fixture = TestBed.createComponent(SellerDashboardComponent);
    fixture.detectChanges();
    expect(localRouter.navigate).toHaveBeenCalledWith(['/login']);
  });
});
