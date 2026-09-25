import argparse
import json
from pathlib import Path
from ai_platform.data.dataset import generate, load
from ai_platform.inference.repository import FileRepository
from ai_platform.training.pipeline import train


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "command", choices=["generate", "validate", "train", "promote", "rollback", "inspect"]
    )
    parser.add_argument("--data", type=Path, default=Path("data/requests-v1.csv"))
    parser.add_argument("--root", type=Path, default=Path("artifacts"))
    parser.add_argument("--version", default="demo-v1")
    parser.add_argument("--strategy", default="linear")
    parser.add_argument("--seed", type=int, default=42)
    args = parser.parse_args()
    repository = FileRepository(args.root)
    if args.command == "generate":
        result = generate(args.data, args.seed)
    elif args.command == "validate":
        result = load(args.data)[1]
    elif args.command == "train":
        result = train(args.data, args.root, args.version, args.strategy, args.seed)
    elif args.command == "promote":
        repository.promote(args.version)
        result = {"champion": args.version}
    elif args.command == "rollback":
        result = {"champion": repository.rollback()}
    else:
        result = repository.load(args.version).metadata
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()
