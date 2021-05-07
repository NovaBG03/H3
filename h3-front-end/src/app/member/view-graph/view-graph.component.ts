import {Component, HostListener, OnDestroy, OnInit} from '@angular/core';
import {Couple, FamilyMember, Gender, Graph} from '../../shared/dtos.model';
import {Subscription} from 'rxjs';
import {MemberService} from '../member.service';
import {ActivatedRoute} from '@angular/router';
import {TreeService} from '../../tree/tree.service';
import {AuthService} from '../../authentication/auth.service';
import {map, switchMap, tap} from 'rxjs/operators';
import * as d3 from 'd3';

@Component({
  selector: 'app-view-graph',
  templateUrl: './view-graph.component.html',
  styleUrls: ['./view-graph.component.css']
})
export class ViewGraphComponent implements OnInit, OnDestroy {
  treeId: number;
  couples: Couple[];

  editingCouple: Couple = null;
  editingMember: FamilyMember = null;
  isCreatingMember = false;
  isCreatingPartner = false;
  isOwner: boolean;
  familyMembers: FamilyMember[];

  rightClickInfo: { x: number, y: number, isPrimary: boolean, isMain: boolean } = null;
  data: Graph =
    {
      nodes: [],
      links: []
    };

  private lastSelectedMemberId: number = null;
  private lastSelectedCouple: Couple = null;
  private userSub: Subscription;

  private simulation;
  private svg;
  private svgContent;
  private nodesSelection;
  private linksSelection;

  private chartHeight = 600;
  private chartWidth = 800;

  private image = 'https://cdn.balkan.app/shared/f1.png';
  private circleRadius = 40;
  private strokeWidth = 3;
  private imageRadius = this.circleRadius - this.strokeWidth;
  private childRatio = 0.8; // from 0 to 1
  private fontSize = 16;

  constructor(private memberService: MemberService,
              private route: ActivatedRoute,
              private treeService: TreeService,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    // this.setUpTemplate();
    // this.createChart();

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

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    this.resetCoordinates();
  }

  onStartCreating(): void {
    this.isCreatingMember = true;
  }

  onEditMember(): void {
    const id = this.lastSelectedMemberId;
    const member = this.familyMembers.find(f => f.id === id);
    this.editingMember = member;
  }

  onAddChild(): void {
    this.editingCouple = this.lastSelectedCouple;
    this.isCreatingMember = true;
  }

  onAddPartner(): void {
    this.editingCouple = this.lastSelectedCouple;
    this.isCreatingPartner = true;
  }

  onDelete(): void {
    this.memberService.deleteMember(this.treeId, this.lastSelectedMemberId)
      .subscribe(
        message => this.onFinishEditing(true),
        error => this.onFinishEditing(false)
      );
  }

  onFinishEditing(isChanged: boolean): void {
    this.lastSelectedCouple = null;
    this.editingCouple = null;
    this.lastSelectedMemberId = null;
    this.editingMember = null;
    this.isCreatingMember = false;
    this.isCreatingPartner = false;
    if (isChanged) {
      // this.loadMembers();
      window.location.reload();
    }
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

  private loadMembers(): void {
    this.memberService.getCouples(this.treeId).subscribe(couples => {
      this.data.nodes = couples;
      if (this.data.nodes.length > 0) {
        this.memberService.getFamilyMembers(this.treeId).subscribe(familyMembers => {
          this.familyMembers = familyMembers;
          if (!this.simulation) {
            this.createGraph();
          } else {
            this.drawGraphElements();
          }
        });
      }
    });
  }

  private createGraph(): void {
    this.simulation = d3.forceSimulation(this.data.nodes)
      .velocityDecay(0.8)
      .alphaDecay(0.01)
      .force('link', d3.forceLink(this.data.links)
        // .distance(0)
        .strength(0.025))
      .force('charge', d3.forceManyBody()
        .strength(-200))
      .force('collide', d3.forceCollide()
        .radius(this.circleRadius * 2.8)
        .strength(1))
      .force('center', d3.forceCenter(this.chartWidth / 2, this.chartHeight / 2))
      .force('x', d3.forceX())
      .force('y', d3.forceY());

    // Create SVG
    this.svg = d3.select('#graph')
      .append('svg')
      .attr('height', this.chartHeight)
      .attr('width', this.chartWidth);

    this.svg.on('contextmenu', e => e.preventDefault());

    this.svgContent = this.svg.append('g');

    this.drawGraphElements();

    // on tick
    this.simulation.on('tick', this.tick());
  }

  private drawGraphElements(): void {
    // Update width (todo add update height)np
    const dynamicWidth = d3.select('#graph').node().getBoundingClientRect().width;
    this.svg.attr('width', dynamicWidth);
    this.simulation.force('center', d3.forceCenter(dynamicWidth / 2, this.chartHeight / 2));

    // Zooming behaviour
    this.svg.call(d3.zoom()
      .scaleExtent([0.1, 5])
      .on('zoom', this.zoomed()));

    // Extract Links and create link force
    this.extractLinks();
    this.simulation.force('link', d3.forceLink(this.data.links));

    // Append SVG Content
    this.svgContent.append('defs')
      .append('svg:marker')
      .attr('id', 'arrow')
      .attr('viewBox', '0 -5 10 10')
      .attr('refX', this.circleRadius / 16)
      .attr('refY', 0)
      .attr('markerWidth', this.circleRadius / 8)
      .attr('markerHeight', this.circleRadius / 8)
      .attr('orient', 'auto')
      .append('svg:path')
      .attr('d', 'M0,-5L10,0L0,5')
      .style('fill', '#999999');

    // Create Links
    const linkUpdateSelection = this.svgContent.selectAll('polyline')
      .data(this.data.links, this.getLinkId);
    this.linksSelection = linkUpdateSelection.enter()
      .append('polyline')
      .attr('stroke', '#999999')
      .attr('stroke-opacity', 0.6)
      .attr('stroke-width', 4)
      .attr('marker-mid', 'url(#arrow)')
      .merge(linkUpdateSelection);

    // Create Nodes
    const nodesUpdateSelection = this.svgContent.selectAll('g.couple')
      .data(this.data.nodes, this.getNodeId);
    this.nodesSelection = nodesUpdateSelection
      .enter()
      .append('g')
      .classed('couple', true)
      .merge(nodesUpdateSelection);

    // Configure Node Dragging
    this.nodesSelection.call(d3.drag()
      .on('start', (e, d) => {
        this.simulation.stop();
        this.resetCoordinates();
      })
      .on('drag', (e, d) => {
        d.x = e.x;
        d.y = e.y;
        this.tick()();
      })
      .on('end', (e, d) => {
        this.tick()();
        this.simulation.restart();
        this.simulation.alpha(0.1);
      }));

    const primaryParentCoupleNodes = this.nodesSelection.filter(d => !!d.partnerParentId);
    const partnerParentCoupleNodes = this.nodesSelection.filter(d => !!d.partnerParentId);
    const childNodes = this.nodesSelection.filter(d => !d.partnerParentId);

    primaryParentCoupleNodes.append('text')
      .attr('text-anchor', 'end')
      .attr('font-size', `${this.fontSize}`)
      .attr('x', -this.circleRadius / 3)
      .attr('y', this.circleRadius + this.strokeWidth + this.fontSize)
      .text(d => d.primaryParentName);

    partnerParentCoupleNodes.append('text')
      .attr('text-anchor', 'start')
      .attr('font-size', `${this.fontSize}`)
      .attr('x', this.circleRadius / 3)
      .attr('y', this.circleRadius + this.strokeWidth + this.fontSize)
      .text(d => d.partnerParentName);

    childNodes.append('text')
      .attr('text-anchor', 'middle')
      .attr('font-size', `${this.fontSize}`)
      .attr('x', 0)
      .attr('y', this.circleRadius * this.childRatio + this.fontSize + 1)
      .text(d => d.primaryParentName);

    // Draw Parent Node Link
    const parentsLinks = this.nodesSelection.filter(d => !!d.partnerParentId);
    parentsLinks.append('circle')
      .attr('fill', '#fff')
      .attr('stroke', '#000')
      .attr('stroke-width', 2 * this.strokeWidth)
      .attr('r', this.circleRadius * this.childRatio);

    // Draw primary parents
    primaryParentCoupleNodes.append('circle')
      .attr('fill', '#fff')
      .attr('stroke', d => this.getStrokeColor(d.primaryParentId))
      .attr('stroke-width', this.strokeWidth)
      .attr('r', this.circleRadius)
      .attr('transform', `translate(${-this.circleRadius - (this.strokeWidth * 0.5)}, 0)`);

    // Draw Partners
    partnerParentCoupleNodes.append('circle')
      .attr('fill', '#fff')
      .attr('stroke', d => this.getStrokeColor(d.partnerParentId))
      .attr('stroke-width', this.strokeWidth)
      .attr('r', this.circleRadius)
      .attr('transform', `translate(${this.circleRadius + (this.strokeWidth * 0.5)}, 0)`);

    // Draw Children
    childNodes.append('circle')
      .attr('fill', '#fff')
      .attr('stroke', d => this.getStrokeColor(d.primaryParentId))
      .attr('stroke-width', this.strokeWidth)
      .attr('r', this.circleRadius * this.childRatio);

    // Draw Images
    const primaryParentImage = primaryParentCoupleNodes.append('svg:image')
      .attr('x', -this.imageRadius - this.circleRadius - (this.strokeWidth * 0.5))
      .attr('y', -this.imageRadius)
      .attr('width', 2 * this.imageRadius)
      .attr('height', 2 * this.imageRadius)
      .attr('xlink:href', this.image)
      .style('cursor', 'pointer');

    const partnerParentImage = partnerParentCoupleNodes.append('svg:image')
      .attr('transform', `translate(${-this.imageRadius})`, 0)
      .attr('x', this.circleRadius + (this.strokeWidth * 0.5))
      .attr('y', -this.imageRadius)
      .attr('width', 2 * this.imageRadius)
      .attr('height', 2 * this.imageRadius)
      .attr('xlink:href', this.image)
      .style('cursor', 'pointer');

    const childImage = childNodes.append('svg:image')
      .attr('x', -this.imageRadius * this.childRatio)
      .attr('y', -this.imageRadius * this.childRatio)
      .attr('width', 2 * this.imageRadius * this.childRatio)
      .attr('height', 2 * this.imageRadius * this.childRatio)
      .attr('xlink:href', this.image)
      .style('cursor', 'pointer');

    // Events
    primaryParentImage
      .on('mouseover', this.handleImageMouseOver)
      .on('mouseout', this.handleImageMouseOut)
      .on('contextmenu', (e, d) => this.handleImageRightClick(e, d, true));

    partnerParentImage
      .on('mouseover', this.handleImageMouseOver)
      .on('mouseout', this.handleImageMouseOut)
      .on('contextmenu', (e, d) => this.handleImageRightClick(e, d, false));

    childImage
      .on('mouseover', this.handleImageMouseOver)
      .on('mouseout', this.handleImageMouseOut)
      .on('contextmenu', (e, d) => this.handleImageRightClick(e, d, true));


    // titles
    childImage.append('title')
      .text(d => d.primaryParentName);

    primaryParentImage.append('title')
      .text(d => d.primaryParentName);

    partnerParentImage.append('title')
      .text(d => d.partnerParentName);
  }

  private tick(): () => void {
    const self = this;
    return () => {
      // @ts-ignore
      self.linksSelection.attr('x1', d => d.source.x)  // @ts-ignore
        .attr('y1', d => d.source.y)  // @ts-ignore
        .attr('x2', d => d.target.x)  // @ts-ignore
        .attr('y2', d => d.target.y);

      self.linksSelection.attr('points', d => `${d.source.x},${d.source.y} ${(d.source.x + d.target.x) / 2},${(d.source.y + d.target.y) / 2} ${d.target.x},${d.target.y}`);

      self.nodesSelection.attr('transform', d => `translate(${d.x}, ${d.y})`);
    };
  }

  private getLinkId(link): number {
    return +('' + link.source.primaryParentId + link.source.partnerParentId + link.target.primaryParentId + link.target.partnerParentId);
  }

  private getNodeId(node): number {
    return +('' + node.primaryParentId + node.partnerParentId);
  }

  private handleImageRightClick(event, data, isPrimary): void {
    this.lastSelectedCouple = data;

    if (isPrimary) {
      this.lastSelectedMemberId = data.primaryParentId;
    } else {
      this.lastSelectedMemberId = data.partnerParentId;
    }

    this.rightClickInfo = {x: event.clientX, y: event.clientY, isPrimary, isMain: 1 === data.leftIndex};
  }

  private handleImageMouseOver(event, data): void {
    d3.select(this)
      .attr('opacity', 0.8);
  }

  private handleImageMouseOut(event, data): void {
    d3.select(this)
      .attr('opacity', 1);
  }

  private getStrokeColor(id: number): string {
    const member = this.familyMembers.find(f => f.id === id);

    if (!member) {
      return '#000';
    }

    switch (member.gender) {
      case Gender.FEMALE:
        return '#ff4500';
      case Gender.MALE:
        return '#039BE5';
      case Gender.UNKNOWN:
        return '#aeaeae';
    }
  }

  private extractLinks(): void {
    const startingDepth = 0;
    const firstNode = this.data.nodes
      .find(d => d.depthIndex === startingDepth);
    this.extractLinksForNode(firstNode);
  }

  private extractLinksForNode(node): void {
    const depthIndex = node.depthIndex;

    const children = this.data.nodes.filter(d =>
      d.depthIndex === depthIndex + 1
      && d.leftIndex > node.leftIndex
      && d.rightIndex < node.rightIndex);

    children.forEach(c => this.data.links.push({
      source: node,
      target: c
    }));

    children.forEach(c => this.extractLinksForNode(c));
  }

  private resetCoordinates(): void {
    if (this.rightClickInfo) {
      this.rightClickInfo = null;
    }
  }

  private zoomed(): (event: any) => void {
    const self = this;
    return ({transform}) => {
      self.svgContent.attr('transform', transform);
    };
  }
}
