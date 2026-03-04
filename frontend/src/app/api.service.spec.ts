import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService } from './api.service';
import { AuthServiceService } from './auth-service.service';

describe('ApiService', () => {
  const baseUrl = 'https://localhost:8443/api';
  let service: ApiService;
  let httpMock: HttpTestingController;
  let authService: jasmine.SpyObj<AuthServiceService>;

  beforeEach(() => {
    authService = jasmine.createSpyObj<AuthServiceService>('AuthServiceService', ['getToken', 'getUser']);
    authService.getToken.and.returnValue('token-123');
    authService.getUser.and.returnValue({ id: 'user-1' });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ApiService,
        { provide: AuthServiceService, useValue: authService }
      ]
    });

    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call user endpoints with expected URLs', () => {
    service.signup({ name: 'A' }).subscribe();
    service.login({ email: 'a@b.com' }).subscribe();
    service.profile('u1').subscribe();
    service.updateProfile('u1', { name: 'New' }).subscribe();
    service.updateRole('u1', { role: 'seller' }).subscribe();
    service.deleteAccount('u1').subscribe();
    service.logout().subscribe();

    const signupReq = httpMock.expectOne(`${baseUrl}/users/signup`);
    expect(signupReq.request.method).toBe('POST');
    signupReq.flush({});

    const loginReq = httpMock.expectOne(`${baseUrl}/users/login`);
    expect(loginReq.request.method).toBe('POST');
    loginReq.flush({});

    const profileReq = httpMock.expectOne(`${baseUrl}/users/profile?userId=u1`);
    expect(profileReq.request.method).toBe('GET');
    expect(profileReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    profileReq.flush({});

    const updateProfileReq = httpMock.expectOne(`${baseUrl}/users/profile-update?userId=u1`);
    expect(updateProfileReq.request.method).toBe('PUT');
    expect(updateProfileReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    updateProfileReq.flush({});

    const updateRoleReq = httpMock.expectOne(`${baseUrl}/users/role-update?userId=u1`);
    expect(updateRoleReq.request.method).toBe('PUT');
    expect(updateRoleReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    updateRoleReq.flush({});

    const deleteReq = httpMock.expectOne(`${baseUrl}/users/delete?userId=u1`);
    expect(deleteReq.request.method).toBe('DELETE');
    expect(deleteReq.request.body).toEqual({ id: 'user-1' });
    expect(deleteReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    deleteReq.flush({});

    const logoutReq = httpMock.expectOne(`${baseUrl}/users/logout`);
    expect(logoutReq.request.method).toBe('POST');
    expect(logoutReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    logoutReq.flush({});
  });

  it('should call product endpoints with auth headers', () => {
    service.getProducts().subscribe();
    service.getUserProducts('u1').subscribe();
    service.getProductById('p1').subscribe();
    service.createProduct({ name: 'P' }).subscribe();
    service.updateProduct('p1', { name: 'P2' }).subscribe();
    service.deleteProduct('p1').subscribe();

    const listReq = httpMock.expectOne(`${baseUrl}/products/list`);
    expect(listReq.request.method).toBe('GET');
    listReq.flush({});

    const userReq = httpMock.expectOne(`${baseUrl}/products/user-products?userId=u1`);
    expect(userReq.request.method).toBe('GET');
    expect(userReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    userReq.flush({});

    const detailReq = httpMock.expectOne(`${baseUrl}/products/details?productId=p1`);
    expect(detailReq.request.method).toBe('GET');
    expect(detailReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    detailReq.flush({});

    const createReq = httpMock.expectOne(`${baseUrl}/products/create`);
    expect(createReq.request.method).toBe('POST');
    expect(createReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    createReq.flush({});

    const updateReq = httpMock.expectOne(`${baseUrl}/products/update?productId=p1`);
    expect(updateReq.request.method).toBe('PUT');
    expect(updateReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    updateReq.flush({});

    const deleteReq = httpMock.expectOne(`${baseUrl}/products/delete?productId=p1`);
    expect(deleteReq.request.method).toBe('DELETE');
    expect(deleteReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    deleteReq.flush({});
  });

  it('should call media endpoints with auth headers', () => {
    const formData = new FormData();
    formData.append('file', new Blob(['data']), 'file.txt');
    formData.append('productId', 'p1');

    service.addmedia(formData).subscribe();
    service.getImagesByProductId('p1').subscribe();
    service.deleteImage({ id: 'img1' }).subscribe();

    const addReq = httpMock.expectOne(`${baseUrl}/media/upload`);
    expect(addReq.request.method).toBe('POST');
    expect(addReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    addReq.flush({});

    const listReq = httpMock.expectOne(`${baseUrl}/media/getImagesByProductId?productId=p1`);
    expect(listReq.request.method).toBe('GET');
    expect(listReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    listReq.flush({});

    const deleteReq = httpMock.expectOne(`${baseUrl}/media/delete`);
    expect(deleteReq.request.method).toBe('DELETE');
    expect(deleteReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    deleteReq.flush({});
  });

  it('should call order endpoints with auth headers', () => {
    service.getCartByUserId('u1').subscribe();
    service.updateCart('u1', { items: [] }).subscribe();
    service.placeOrder('u1', { items: [] }).subscribe();
    service.getOrderById('o1', 'u1').subscribe();
    service.getOrdersByUserId('u1').subscribe();
    service.getSellerOrders('s1').subscribe();
    service.updateOrderStatus('u1', { status: 'SHIPPED' }).subscribe();
    service.deleteOrder('o1', 'u1').subscribe();
    service.getClientData('u1').subscribe();

    const cartReq = httpMock.expectOne(`${baseUrl}/orders/cart?userId=u1`);
    expect(cartReq.request.method).toBe('GET');
    expect(cartReq.request.headers.get('Authorization')).toBe('Bearer token-123');
    cartReq.flush({});

    const updateReq = httpMock.expectOne(`${baseUrl}/orders/update-cart?userId=u1`);
    expect(updateReq.request.method).toBe('PUT');
    updateReq.flush({});

    const placeReq = httpMock.expectOne(`${baseUrl}/orders/place-order?userId=u1`);
    expect(placeReq.request.method).toBe('POST');
    placeReq.flush({});

    const orderReq = httpMock.expectOne(`${baseUrl}/orders/order-details?orderId=o1&userId=u1`);
    expect(orderReq.request.method).toBe('GET');
    orderReq.flush({});

    const ordersReq = httpMock.expectOne(`${baseUrl}/orders/user-orders?userId=u1`);
    expect(ordersReq.request.method).toBe('GET');
    ordersReq.flush({});

    const sellerReq = httpMock.expectOne(`${baseUrl}/orders/seller-orders?sellerId=s1`);
    expect(sellerReq.request.method).toBe('GET');
    sellerReq.flush({});

    const statusReq = httpMock.expectOne(`${baseUrl}/orders/update-order-status?userId=u1`);
    expect(statusReq.request.method).toBe('POST');
    statusReq.flush({});

    const deleteReq = httpMock.expectOne(`${baseUrl}/orders/delete-order?orderId=o1&userId=u1`);
    expect(deleteReq.request.method).toBe('DELETE');
    deleteReq.flush({});

    const clientReq = httpMock.expectOne(`${baseUrl}/orders/client-dashboard-data?userId=u1`);
    expect(clientReq.request.method).toBe('GET');
    clientReq.flush({});
  });
});
