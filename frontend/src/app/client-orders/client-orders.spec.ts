import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClientOrders } from './client-orders';
import { ActivatedRoute } from '@angular/router';

describe('ClientOrders', () => {
  let component: ClientOrders;
  let fixture: ComponentFixture<ClientOrders>;

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: (key: string) => 'user-id'
      }
    },
    params: {
        subscribe: () => ({})
    },
    queryParamMap: {
        subscribe: () => ({})
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientOrders],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientOrders);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
