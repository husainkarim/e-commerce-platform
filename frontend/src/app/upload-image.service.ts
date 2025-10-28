import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UploadImageService {
  file: File | null = null;
  preview: string | null = null;

  constructor() {}

  createName(): string {
    if (!this.file) {
      throw new Error('No file selected');
    }
    const timestamp = Date.now();
    const fileName = this.file.name.split('.')[0];
    const fileExtension = this.file.name.split('.').pop();
    return `${fileName}_${timestamp}.${fileExtension}`;
  }

  uploadFile(file: File) {
    this.file = file;

    if (!this.file) {
      throw new Error('No file selected');
    }
    this.preview = URL.createObjectURL(this.file);
    console.log('File preview URL:', this.preview);
  }
}
