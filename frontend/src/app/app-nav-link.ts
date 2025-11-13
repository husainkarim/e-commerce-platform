import { Component, ChangeDetectionStrategy, Input, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { routerMock } from './router-mock';

@Component({
  selector: 'app-nav-link',
  standalone: true,
  imports: [CommonModule],
  template: ``,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppNavLink {
  @Input({ required: true }) path!: string;

  router = routerMock;
  isActive = computed(() => this.router.currentUrl$() === this.path);

  navigateTo() {
    this.router.navigate([this.path]);
  }
}
