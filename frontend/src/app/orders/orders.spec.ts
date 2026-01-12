import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Orders } from './orders';
import { ActivatedRoute } from '@angular/router';

describe('Orders', () => {
  let component: Orders;
  let fixture: ComponentFixture<Orders>;

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
      imports: [Orders],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: mockActivatedRoute
        }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Orders);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
