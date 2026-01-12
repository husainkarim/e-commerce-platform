import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Cart } from './cart';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { AuthServiceService } from '../auth-service.service';
import { ApiService } from '../api.service';

describe('Cart', () => {
  let component: Cart;
  let fixture: ComponentFixture<Cart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Cart, HttpClientTestingModule, RouterTestingModule],
      providers: [
      {
        provide: ActivatedRoute,
        useValue: {
          params: of({}),
          queryParams: of({}),
          snapshot: {
            paramMap: {
              get: () => null
            }
          }
        }
      },
      {
        provide: AuthServiceService,
        useValue: {
          isLoggedIn: () => true,
          getUser: () => ({ id: 1, name: 'Test User' }),
          getToken: () => 'fake-jwt-token'
        }
      },
      {
        provide: ApiService,
        useValue: {
          GetCartByUserId: () =>
            of({
              items: []
            }),
          UpdateCart: () =>
            of({
              success: true
            })
        }
      }
    ]

    })
    .compileComponents();

    fixture = TestBed.createComponent(Cart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
