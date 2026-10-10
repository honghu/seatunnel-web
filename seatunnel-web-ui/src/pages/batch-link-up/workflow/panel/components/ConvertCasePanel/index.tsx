import { Button, Form, Input, message, Radio } from "antd";
import { memo, useEffect, useMemo } from "react";
import PanelShell from "../PanelShell";

const { TextArea } = Input;

interface Props {
  selectedNode: any;
  onClose: () => void;
  onNodeDataChange: (nodeId: string, newData: any) => void;
  getDirectUpstreamSchema: (nodeId: string) => any[];
  refreshNodeSchema: (nodeId: string) => void;
  refreshDownstreamSchemas: (nodeId: string) => void;

  /**
   * 根据画布连接关系同步：
   * pluginInput = 直接上游节点 ID
   * pluginOutput = 直接下游节点 ID
   */
  syncTransformPluginConfig: (nodeId: string) => {
    pluginInput?: string;
    pluginOutput?: string;
  };
}

function ConvertCasePanel({
  selectedNode,
  onClose,
  onNodeDataChange,
  getDirectUpstreamSchema,
  refreshNodeSchema,
  refreshDownstreamSchemas,
  syncTransformPluginConfig,
}: Props) {
  const nodeId = selectedNode?.id;

  const title =
    selectedNode?.data?.title || selectedNode?.data?.label || "SQL 脚本";

  const description =
    selectedNode?.data?.description || "字母大小写转换,追加字段的前/后缀";

  const config = selectedNode?.data?.config || {};
  const convertCase = config.convertCase || "";
  const prefix = config.prefix || "";
  const suffix = config.suffix || "";

  const upstreamSchema = useMemo(() => {
    if (!nodeId) {
      return [];
    }

    return getDirectUpstreamSchema(nodeId) || [];
  }, [nodeId, getDirectUpstreamSchema]);

  /**
   * 同步上游字段元数据。
   */
  useEffect(() => {
    if (!nodeId) {
      return;
    }

    onNodeDataChange(nodeId, {
      meta: {
        inputSchema: upstreamSchema,
      },
    });
  }, [nodeId, upstreamSchema, onNodeDataChange]);

  const handleSqlChange = ({
    convertCase,
    prefix,
    suffix,
  }: {
    convertCase?: string;
    prefix?: string;
    suffix?: string;
  }) => {
    if (!nodeId) {
      return;
    }

    /**
     * 必须保留已有 config。
     *
     * 否则当 onNodeDataChange 是浅合并时，
     * 修改 SQL 可能会把已经写入的 pluginInput/pluginOutput 覆盖掉。
     */
    onNodeDataChange(nodeId, {
      config: {
        ...config,
        convertCase,
        prefix,
        suffix,
      },
    });
  };

  const handleApply = () => {
    if (!nodeId) {
      message.warning("当前节点不存在");
      return;
    }

    const nextConvertCase = String(convertCase || "");
    const nextPrefix = String(prefix || "").trim();
    const nextSuffix = String(suffix || "").trim();
    // if (!nextSql) {
    //   message.warning("请输入 SQL 转换脚本");
    //   return;
    // }

    /**
     * 参考 FieldMapper：
     * 从当前画布连接关系重新获取上下游节点。
     */
    const syncedPluginConfig = syncTransformPluginConfig(nodeId) || {};

    const pluginInput = syncedPluginConfig.pluginInput;
    const pluginOutput = syncedPluginConfig.pluginOutput;

    if (!pluginInput) {
      message.warning("请先连接上游节点");
      return;
    }

    if (!pluginOutput) {
      message.warning("请先连接下游节点");
      return;
    }

    onNodeDataChange(nodeId, {
      config: {
        ...config,
        convertCase: nextConvertCase,
        prefix: nextPrefix,
        suffix: nextSuffix,
        /**
         * 前端节点配置使用 camelCase。
         */
        pluginInput,
        pluginOutput,
      },
      meta: {
        ...(selectedNode?.data?.meta || {}),
        inputSchema: upstreamSchema,
      },
    });

    /**
     * 解析当前 SQL 节点输出字段，
     * 并继续刷新后续节点的输入字段。
     */
    refreshNodeSchema(nodeId);
    refreshDownstreamSchemas(nodeId);

    message.success("重命名脚本已应用");
  };

  return (
    <PanelShell
      eyebrow="Transform Config"
      title="SQL 转换"
      badge="处理节点"
      desc="基于上游字段编写自定义转换逻辑"
      heroTitle={title}
      heroDesc={description}
      heroTag="TRANSFORM"
      onClose={onClose}
    >
      <section className="workflow-panel__section">
        <div className="workflow-panel__section-head">
          <div className="workflow-panel__section-title">脚本配置</div>
        </div>
        {/* <div className="workflow-panel__section-tip">字母大小写转换</div> */}

        {/* <TextArea
          value={convertCase}
          onChange={(event) => handleSqlChange(event.target.value)}
          placeholder="请输入 SQL 转换脚本"
          autoSize={{
            minRows: 10,
            maxRows: 16,
          }}
        /> */}
        <Radio.Group
          value={convertCase}
          onChange={(event) => {
            handleSqlChange({
              convertCase: event.target.value,
              prefix,
              suffix,
            });
          }}
          options={[
            { label: "字母转大写", value: "UPPER" },
            { label: "字母转小写", value: "LOWER" },
          ]}
        />
        <Form>
          <Form.Item label="追加到字段名的前缀">
            <Input
              value={prefix}
              onChange={(event) =>
                handleSqlChange({
                  convertCase,
                  prefix: event.target.value,
                  suffix,
                })
              }
              placeholder="请输入追加到字段名的前缀"
              // value={prefix}
              // onChange={(event) => handleSqlChange(event.target.value)}
            />
          </Form.Item>
          <Form.Item label="追加到字段名的后缀">
            <Input
              placeholder="请输入追加到字段名的后缀"
              value={suffix}
              onChange={(event) =>
                handleSqlChange({
                  suffix: event.target.value,
                  convertCase,
                  prefix,
                })
              }
              // value={prefix}
              // onChange={(event) => handleSqlChange(event.target.value)}
            />
          </Form.Item>
        </Form>
        <div style={{ marginTop: 12 }}>
          <Button type="primary" onClick={handleApply}>
            应用脚本
          </Button>
        </div>
      </section>
    </PanelShell>
  );
}

export default memo(ConvertCasePanel);
