package io.inoa.fleet.ui.views;

import io.inoa.fleet.registry.management.GatewayPageVO;
import io.inoa.fleet.registry.management.GatewayVO;
import io.inoa.fleet.ui.internal.AbstractListModel;
import io.micronaut.core.annotation.Introspected;
import lombok.Getter;

@Introspected
@Getter
public class GatewayListModel extends AbstractListModel<GatewayVO> {

	public GatewayListModel(int page, int pageSize, GatewayPageVO vo, String search, String sort) {
		super(page, pageSize, vo.getTotalSize(), vo.getContent(), search, sort);
	}
}
