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
        },
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
          getCartByUserId: (id: string) =>
            of({
              cart: {
                items: []
              }
            }),
          getProductById: () =>
            of({
              id: '1',
              name: 'Test Product',
              description: '',
              price: 10,
              userId: 'u1',
              image: '',
              category: ''
            }),
          updateCart: () =>
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
