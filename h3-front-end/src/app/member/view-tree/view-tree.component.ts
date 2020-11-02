import {Component, OnDestroy, OnInit} from '@angular/core';
import {MemberService} from '../member.service';
import {ActivatedRoute} from '@angular/router';
import OrgChart from '@balkangraph/orgchart.js';
import {FamilyMember, FamilyMembers} from '../../shared/dtos.model';
import {map, switchMap, tap} from 'rxjs/operators';
import {TreeService} from '../../tree/tree.service';
import {AuthService} from '../../authentication/auth.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-view-tree',
  templateUrl: './view-tree.component.html',
  styleUrls: ['./view-tree.component.css']
})
export class ViewTreeComponent implements OnInit, OnDestroy {
  treeId: number;
  familyMembers: FamilyMembers;
  editingMember: FamilyMember = null;
  isCreatingMember = false;
  isOwner: boolean;
  private userSub: Subscription;
  private chart: OrgChart;

  constructor(private memberService: MemberService,
              private route: ActivatedRoute,
              private treeService: TreeService,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    this.setUpTemplate();
    this.createChart();

    this.userSub = this.route.parent.url.pipe(
      map(parentUrlSegment => +parentUrlSegment[0]),
      tap(treeId => this.treeId = treeId),
      switchMap(treeId => {
        return this.treeService.getTree(treeId).pipe(
          switchMap(tree => {
            return this.authService.user.pipe(
              map(user => user.username === tree.owner)
            );
          }));
      })
    ).subscribe(isOwner => {
      this.isOwner = isOwner;
      this.loadMembers();
    });
  }

  onStartCreating(): void {
    this.isCreatingMember = true;
  }

  onFinishEditing(isChanged: boolean): void {
    this.editingMember = null;
    this.isCreatingMember = false;
    if (isChanged) {
      this.loadMembers();
    }
  }

  // pid - partner id; tags: ['partner']

  private setUpTemplate(): void {
    OrgChart.templates.family_template = Object.assign({}, OrgChart.templates.ana);
    OrgChart.templates.family_template.size = [86, 86];
    OrgChart.templates.family_template.plus = '';
    OrgChart.templates.family_template.minus = '';
    OrgChart.templates.family_template.rippleRadius = 40;
    OrgChart.templates.family_template.name = '<text style="font-size: 12px;" fill="#000000" x="43" y="100" text-anchor="middle">{val}</text>';
    OrgChart.templates.family_template.title = '<text style="font-size: 12px;" fill="#aeaeae" x="43" y="115" text-anchor="middle">{val}</text>';
    OrgChart.templates.family_template.img = '<clipPath id="{randId}"><circle cx="43" cy="43" r="38.5"></circle></clipPath></circle><image preserveAspectRatio="xMidYMid slice" clip-path="url(#{randId})" xlink:href="{val}" x="3" y="3"  width="80" height="80"></image>';
    OrgChart.templates.family_template.node = '<circle stroke-width="3" fill="none" stroke="#aeaeae" cx="43" cy="43" r="41.5"></circle>';
    OrgChart.templates.family_template.defs = '<g transform="matrix(0.05,0,0,0.05,-13,-15.5)" id="baby"><circle cx="260" cy="310" r="200" fill="#ffffff"></circle><path fill="#aeaeae" d="m468.64 268.32h-13.591c-18.432-89.348-95.612-157.432-189.139-161.798-.501-.185-1.015-.348-1.545-.482-18.363-4.622-31.188-22.595-31.188-43.707 0-17.828 14.468-32.333 32.252-32.333 12.573 0 22.802 10.258 22.802 22.866 0 8.284 6.716 15 15 15s15-6.716 15-15c0-29.15-23.687-52.866-52.802-52.866-34.326 0-62.252 27.962-62.252 62.333 0 17.876 5.828 34.443 15.769 47.432-80.698 15.127-144.725 78.25-161.291 158.555h-13.591c-24.103 0-43.712 19.596-43.712 43.683 0 24.086 19.609 43.682 43.712 43.682h14.692c20.935 89.871 101.582 157.018 197.596 157.018s176.66-67.148 197.596-157.018h14.692c24.103 0 43.712-19.596 43.712-43.682 0-24.087-19.609-43.683-43.712-43.683zm-265.054 55.257c-8.284-.024-14.981-6.758-14.958-15.043.007-2.337-.708-13.999-15.481-14.041-.026 0-.053 0-.08 0-14.697 0-15.475 11.62-15.481 13.953-.023 8.284-6.75 15.007-15.043 14.957-8.284-.024-14.98-6.759-14.957-15.043.038-13.322 5.349-25.101 14.955-33.166 8.223-6.904 19.065-10.702 30.543-10.702h.148c11.534.033 22.412 3.896 30.63 10.876 9.559 8.12 14.803 19.928 14.765 33.25-.023 8.27-6.735 14.957-14.999 14.957-.013.002-.027.002-.042.002zm52.766 129.374c-26.485 0-48.033-21.533-48.033-48.002 0-8.284 6.716-15 15-15s15 6.716 15 15c0 9.926 8.089 18.002 18.033 18.002s18.033-8.076 18.033-18.002c0-8.284 6.716-15 15-15s15 6.716 15 15c-.001 26.469-21.548 48.002-48.033 48.002zm113.765-129.374c-.015 0-.029 0-.044 0-8.284-.024-14.98-6.759-14.957-15.043.016-5.445-1.993-9.263-6.14-11.673-5.407-3.142-13.27-3.165-18.695-.053-4.161 2.387-6.191 6.193-6.207 11.638-.023 8.27-6.735 14.957-14.999 14.957-.015 0-.029 0-.043 0-8.284-.024-14.981-6.758-14.958-15.043.046-16.149 7.802-29.845 21.281-37.576 14.814-8.497 33.929-8.443 48.695.138 13.434 7.807 21.112 21.547 21.066 37.696-.023 8.271-6.735 14.959-14.999 14.959z"/> </g>';


    OrgChart.templates.family_template_blue = Object.assign({}, OrgChart.templates.family_template);
    OrgChart.templates.family_template_blue.node = '<circle stroke-width="3" fill="none" stroke="#039BE5" cx="43" cy="43" r="41.5"></circle>';
  }

  private createChart(): void {
    this.chart = new OrgChart(document.getElementById('tree'), {
      template: 'family_template',
      nodeMouseClick: OrgChart.action.none, // disables default onClick behaviour
      enableSearch: false, // set to true if you want to use search
      siblingSeparation: 100,
      nodeBinding: {
        name: 'name',
        title: 'title',
        img: 'img',
      },
      tags: {
        blue: {
          template: 'family_template_blue'
        }
      }
    });

    // sub for click event
    this.chart.on('click', (sender, node) => {
      const currentNode = node.node;
      this.editingMember = this.familyMembers.getMember(currentNode.id);
    });


    // sub for double click event
    // chart.on('dbclick', () => {
    //   console.log('double click');
    // });

    this.chart.on('render-link', (sender, args) => {
      if (args.cnode.ppid !== undefined) {
        args.html += '<use xlink:href="#baby" x="' + args.p.xa + '" y="' + args.p.ya + '"/>';
      }
    });
  }

  // pid - directHeir; ppid - directHeirSpouse
  private loadMembers(): void {
    this.memberService.getMembers(this.treeId).subscribe(familyMembers => {
      this.familyMembers = familyMembers;
      if (this.familyMembers.members.length > 0) {
        this.loadChartData();
      }
    });
  }

  private loadChartData(): void {
    const mapped = this.familyMembers.members.map((member) => {
      const tags: string[] = [];
      let pid: number = null;
      let ppid: number = null;

      const isDirectHeir = member.isDirectHeir;
      const firstId = this.familyMembers.mainMember.id;

      if (!isDirectHeir && member.partners.length > 0 && member.id !== firstId) {
        const partnerId = member.partners[0];
        pid = partnerId;
        tags.push('partner');
      }

      if (isDirectHeir) {
        pid = member.primaryParentId;
        ppid = member.secondaryParentId;
      }

      return {
        id: member.id,
        name: member.firstName + ' ' + member.lastName,
        img: 'https://th.thgim.com/sci-tech/technology/internet/article17759222.ece/ALTERNATES/FREE_660/02th-egg-person',
        pid,
        ppid,
        tags
      };
    });

    this.chart.load(mapped);

    // this.chart.load([
    //   { id: 1, tags: ['blue'], name: 'King George VI', img: 'https://cdn.balkan.app/shared/f1.png'},
    //   { id: 2, pid: 1, tags: ['partner'], name: 'Queen Elizabeth', title: 'The Queen Mother', img: 'https://cdn.balkan.app/shared/f2.png' },
    //   { id: 3, pid: 1, ppid: 2, tags: ['blue'], name: 'Queen Elizabeth II', img: 'https://cdn.balkan.app/shared/f5.png'},
    //   { id: 4, pid: 3, tags: ['partner'], name: 'Prince Philip', title: 'Duke of Edinburgh', img: 'https://cdn.balkan.app/shared/f3.png'},
    //   { id: 5, pid: 1, ppid: 2, name: 'Princess Margaret', img: 'https://cdn.balkan.app/shared/f6.png'},
    //   { id: 6, pid: 3, tags: ['blue'], ppid: 4, name: 'Charles', title: 'Prince of Wales', img: 'https://cdn.balkan.app/shared/f8.png'},
    //   { id: 7, pid: 6, tags: ['partner'] , name: 'Diana', title: 'Princess of Wales', img: 'https://cdn.balkan.app/shared/f9.png'},
    //   { id: 8, pid: 6, tags: ['partner'], name: 'Camila', title: 'Duchess of Cornwall', img: 'https://cdn.balkan.app/shared/f7.png' },
    //   { id: 9, pid: 3, ppid: 4 , name: 'Anne', title: 'Princess Royal', img: 'https://cdn.balkan.app/shared/f10.png'},
    //   { id: 10, pid: 3, ppid: 4 , name: 'Prince Andrew', title: 'Duke of York', img: 'https://cdn.balkan.app/shared/f11.png'},
    //   { id: 11, pid: 3, ppid: 4, name: 'Prince Edward', title: 'Earl of Wessex', img: 'https://cdn.balkan.app/shared/f12.png'},
    //   { id: 12, pid: 6, ppid: 7, tags: ['blue'], name: 'Prince William', title: 'Duch of Cambridge', img: 'https://cdn.balkan.app/shared/f14.png'},
    //   { id: 13, pid: 6, ppid: 7, name: 'Prince Harry', img: 'https://cdn.balkan.app/shared/f15.png'},
    //   { id: 14, pid: 12, tags: ['partner'], name: 'Catherine', title: 'Duchess of Cambridge', img: 'https://cdn.balkan.app/shared/f13.png'},
    //   { id: 15, pid: 13, tags: ['partner'], name: 'Meghan Markle', img: 'https://cdn.balkan.app/shared/f16.png'},
    //   { id: 16, pid: 12, ppid: 14, tags: ['blue'], name: 'Prince George of Cambridge', img: 'https://cdn.balkan.app/shared/f17.png'},
    //   { id: 17, pid: 12, ppid: 14, tags: ['blue'], name: 'Prince Charlotte of Cambridge', img: 'https://cdn.balkan.app/shared/f18.png'},
    //   { id: 18, pid: 12, ppid: 14, tags: ['blue'], name: 'Prince Louis of Cambridge', img: 'https://cdn.balkan.app/shared/f19.png'}
    // ]);
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }
}
