package org.signalml.plugin.newstager.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.signalml.app.model.signal.PagingParameterDescriptor;
import org.signalml.plugin.data.io.PluginTagWriterConfig;
import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.data.tag.PluginTagGroup;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.io.PluginTagWriter;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerData;
import org.signalml.plugin.newstager.data.tag.NewStagerTagCollectionType;
import org.signalml.util.Util;

public class NewStagerTagWriter {

	private final NewStagerData stagerData;
	private final NewStagerConstants constants;

	public NewStagerTagWriter(final NewStagerData stagerData,
			NewStagerConstants constants) {
		this.stagerData = stagerData;
		this.constants = constants;
	}

	public File writeTags(NewStagerTagCollectionType tagType,
			EnumSet<NewStagerTagCollectionType> stages,
			Map<NewStagerTagCollectionType, Collection<IPluginTagDef>> tagMap)
			throws IOException, SignalMLException {

		List<PluginTagGroup> sleepTags = new LinkedList<PluginTagGroup>();
		for (Entry<NewStagerTagCollectionType, Collection<IPluginTagDef>> entry : tagMap
				.entrySet()) {

			if (stages.contains(entry.getKey())) {
				Collection<IPluginTagDef> tagCollection = entry.getValue();
				if (!tagCollection.isEmpty()) {
					NewStagerTagCollectionType key = entry.getKey();
					sleepTags.add(new PluginTagGroup(this
							.getTagNameFromType(key), this
							.getGroupTypeFromCollectionType(tagType),
							tagCollection, 1, this
									.getTagDescriptionFromType(key)));
				}
			}
		}

		File resultFile = this.getTagFileName(tagType);
		PluginTagWriter pluginTagWriter = new PluginTagWriter(resultFile,
				new PluginTagWriterConfig(this.constants.blockLengthInSeconds,
						PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE));
		pluginTagWriter.writeTags(sleepTags);

		return resultFile;
	}

	private File getTagFileName(NewStagerTagCollectionType key) {
		String path = this.stagerData.getProjectPath();
		String bookPath = this.stagerData.getParameters().bookFilePath;
		String bookName = Util.getFileNameWithoutExtension(new File(bookPath));
		String ext = "test.tag";

		switch (key) {
		case HYPNO_DELTA:
			return new File(path, bookName + "_delta" + ext);
		case HYPNO_ALPHA:
			return new File(path, bookName + "_alpha" + ext);
		case HYPNO_SPINDLE:
			return new File(path, bookName + "_spindles" + ext);
		case SLEEP_PAGES:
			return new File(path, bookName + "_PrimHypnos_a" + ext);
		case CONSOLIDATED_SLEEP_PAGES:
			return new File(path, bookName + "_hypnos_a" + ext);
		default:
			return null;
		}
	}

	private String getTagNameFromType(NewStagerTagCollectionType tagType) {
		switch (tagType) {
		case HYPNO_ALPHA:
			return "A";
		case HYPNO_DELTA:
			return "S";
		case HYPNO_SPINDLE:
			return "W";
		case SLEEP_STAGE_1:
		case CONSOLIDATED_SLEEP_STAGE_1:
			return "1";
		case SLEEP_STAGE_2:
		case CONSOLIDATED_SLEEP_STAGE_2:
			return "2";
		case SLEEP_STAGE_3:
		case CONSOLIDATED_SLEEP_STAGE_3:
			return "3";
		case SLEEP_STAGE_4:
		case CONSOLIDATED_SLEEP_STAGE_4:
			return "4";
		case SLEEP_STAGE_R:
		case CONSOLIDATED_SLEEP_STAGE_REM:
			return "r";
		case SLEEP_STAGE_W:
		case CONSOLIDATED_SLEEP_STAGE_W:
			return "w";
		case CONSOLIDATED_SLEEP_STAGE_M:
			return "m";
		default:
			return "";
		}
	}

	private String getTagDescriptionFromType(NewStagerTagCollectionType tagType) {
		switch (tagType) {
		case HYPNO_ALPHA:
			return "artifact";
		case HYPNO_SPINDLE:
			return "Stage W";
		case SLEEP_STAGE_1:
		case CONSOLIDATED_SLEEP_STAGE_1:
			return "Stage 1";
		case SLEEP_STAGE_2:
		case CONSOLIDATED_SLEEP_STAGE_2:
			return "Stage 2";
		case SLEEP_STAGE_3:
		case CONSOLIDATED_SLEEP_STAGE_3:
			return "Stage 3";
		case SLEEP_STAGE_4:
		case CONSOLIDATED_SLEEP_STAGE_4:
			return "Stage 4";
		case SLEEP_STAGE_R:
		case CONSOLIDATED_SLEEP_STAGE_REM:
			return "Stage REM";
		case SLEEP_STAGE_W:
		case CONSOLIDATED_SLEEP_STAGE_W:
			return "Stage W";
		case CONSOLIDATED_SLEEP_STAGE_M:
			return "MT";
		default:
			return "";
		}

	}

	private SignalSelectionType getGroupTypeFromCollectionType(
			NewStagerTagCollectionType tagType) {
		switch (tagType) {
		case CONSOLIDATED_SLEEP_PAGES:
		case SLEEP_PAGES:
			return SignalSelectionType.PAGE;
		default:
			return SignalSelectionType.CHANNEL;
		}
	}
}
