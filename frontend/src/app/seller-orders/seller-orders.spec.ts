import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SellerOrders } from './seller-orders';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthServiceService } from '../auth-service.service';

describe('SellerOrders', () => {
  let component: SellerOrders;
  let fixture: ComponentFixture<SellerOrders>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SellerOrders, HttpClientTestingModule],
      providers: [
        {
          provide: AuthServiceService,
          useValue: {
            isLoggedIn: () => true,
            getUser: () => ({ id: 1, name: 'Test User' }),
            getToken: () => 'fake-jwt-token'
          }
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SellerOrders);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
