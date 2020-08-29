package org.oddlama.vane.trifles;

import org.oddlama.vane.core.Core;
import org.oddlama.vane.annotation.VaneModule;
import org.oddlama.vane.core.module.Module;
import org.oddlama.vane.core.item.ModelDataEnum;

@VaneModule(name = "trifles", bstats = 8644, config_version = 1, lang_version = 1, storage_version = 1)
public class Trifles extends Module<Trifles> {
	public Trifles() {
		var fast_walking_group = new FastWalkingGroup(this);
		new FastWalkingListener(fast_walking_group);
		new DoubleDoorListener(this);
		new HarvestListener(this);

		new org.oddlama.vane.trifles.items.Sickle(this);
	}

	@Override
	public Class<? extends ModelDataEnum> model_data_enum() {
		return org.oddlama.vane.trifles.items.ModelData.class;
	}

	@Override
	public int model_data(int id) {
		return Core.model_data(0, id);
	}
}
