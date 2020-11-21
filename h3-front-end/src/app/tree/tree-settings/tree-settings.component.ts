import {Component, OnInit} from '@angular/core';
import {FormArray, FormControl, FormGroup} from '@angular/forms';
import {FamilyTree} from '../../shared/dtos.model';
import {TreeService} from '../tree.service';
import {ActivatedRoute} from '@angular/router';
import {map, switchMap} from 'rxjs/operators';

@Component({
  selector: 'app-tree-settings',
  templateUrl: './tree-settings.component.html',
  styleUrls: ['./tree-settings.component.css']
})
export class TreeSettingsComponent implements OnInit {
  settingsForm: FormGroup;
  tree: FamilyTree;

  constructor(private treeService: TreeService, private route: ActivatedRoute) {
  }

  get tagsFormArray(): FormArray {
    return this.settingsForm.get('tags') as FormArray;
  }

  ngOnInit(): void {
    this.initForm();
    this.route.parent.url.pipe(
      map(parentUrlSegment => +parentUrlSegment[0].path),
      switchMap(treeId => this.treeService.getTree(treeId))
    ).subscribe(familyTree => {
      this.tree = familyTree;
      this.loadFormData();
    });
  }

  addTagControl(): void {
    this.tagsFormArray.push(new FormControl());
  }

  onUpdate(): void {
    const value = this.settingsForm.value;
    this.tree.name = value.name;
    this.tree.isPrivate = value.isPrivate;
    this.tree.tags = this.tagsFormArray.controls.map(control => control.value).filter(tag => tag);
    console.log(this.tree);
    this.treeService.updateTree(this.tree)
      .subscribe(familyTree => {
        this.tree = familyTree;
        this.loadFormData();
      });
  }

  removeTagControl(index: number): void {
    this.tagsFormArray.removeAt(index);
  }

  private loadFormData(): void {
    this.settingsForm.get('name').setValue(this.tree.name);
    this.settingsForm.get('isPrivate').setValue(this.tree.isPrivate);

    this.tagsFormArray.clear();
    this.tree.tags.forEach(tag => {
      this.tagsFormArray.push(new FormControl(tag));
    });
  }

  private initForm(): void {
    this.settingsForm = new FormGroup({
      name: new FormControl(),
      isPrivate: new FormControl(),
      tags: new FormArray([])
    });
  }
}
