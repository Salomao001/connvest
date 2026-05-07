import { Component, Input, forwardRef, HostListener, ElementRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { CommonModule } from '@angular/common';

export interface SelectOption {
  value: string;
  label: string;
}

@Component({
  selector: 'app-select',
  standalone: true,
  imports: [CommonModule],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => AppSelectComponent),
    multi: true
  }],
  host: { class: 'block relative' },
  template: `
    <button type="button" (click)="toggle()"
      class="app-input flex h-10 w-full cursor-pointer items-center justify-between gap-2 bg-white pr-2.5 text-left"
      [ngClass]="{'border-blue-400 ring-2 ring-blue-100': isOpen}">
      <span class="truncate text-sm" [ngClass]="currentValue ? 'text-slate-800' : 'text-slate-400'">
        {{ selectedLabel }}
      </span>
      <svg class="h-4 w-4 shrink-0 text-slate-400 transition-transform duration-150"
           [ngClass]="{'rotate-180': isOpen}"
           fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" d="M19.5 8.25l-7.5 7.5-7.5-7.5"/>
      </svg>
    </button>

    <div *ngIf="isOpen"
      class="absolute left-0 right-0 top-[calc(100%+4px)] z-[200] max-h-60 overflow-y-auto rounded-lg border border-slate-200 bg-white shadow-lg">
      <button *ngFor="let opt of options" type="button" (click)="select(opt)"
        class="flex w-full items-center justify-between gap-2 px-3 py-2.5 text-left text-sm transition-colors"
        [ngClass]="currentValue === opt.value
          ? 'bg-blue-50 font-semibold text-blue-700'
          : 'text-slate-700 hover:bg-slate-50'">
        <span>{{ opt.label }}</span>
        <svg *ngIf="currentValue === opt.value"
          class="h-4 w-4 shrink-0 text-blue-700"
          fill="none" stroke="currentColor" stroke-width="2.5" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.75l6 6 9-13.5"/>
        </svg>
      </button>
    </div>
  `
})
export class AppSelectComponent implements ControlValueAccessor {
  @Input() options: SelectOption[] = [];
  @Input() placeholder = 'Selecionar';

  isOpen = false;
  currentValue = '';

  private onChange = (_: string) => {};
  private onTouched = () => {};

  constructor(private el: ElementRef) {}

  get selectedLabel(): string {
    return this.options.find(o => o.value === this.currentValue)?.label ?? this.placeholder;
  }

  toggle() { this.isOpen = !this.isOpen; this.onTouched(); }

  select(opt: SelectOption) {
    this.currentValue = opt.value;
    this.onChange(this.currentValue);
    this.isOpen = false;
  }

  writeValue(value: string): void { this.currentValue = value ?? ''; }
  registerOnChange(fn: any): void { this.onChange = fn; }
  registerOnTouched(fn: any): void { this.onTouched = fn; }

  @HostListener('document:click', ['$event'])
  onOutsideClick(event: Event) {
    if (!this.el.nativeElement.contains(event.target as Node)) {
      this.isOpen = false;
    }
  }
}
